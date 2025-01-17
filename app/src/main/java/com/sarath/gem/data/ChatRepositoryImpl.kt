package com.sarath.gem.data

import android.util.Log
import com.sarath.gem.core.ai.GemAIModel
import com.sarath.gem.core.ai.SystemAIModel
import com.sarath.gem.core.util.safeTry
import com.sarath.gem.core.util.suspendedSafeTry
import com.sarath.gem.data.local.dao.ConversationDao
import com.sarath.gem.data.local.dao.MessageDao
import com.sarath.gem.data.local.dao.PromptDao
import com.sarath.gem.data.local.mapper.toDomain
import com.sarath.gem.data.local.mapper.toEntity
import com.sarath.gem.data.local.model.ConversationEntity
import com.sarath.gem.data.local.model.MessageStatus
import com.sarath.gem.data.local.model.PromptEntity
import com.sarath.gem.domain.RequestError
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.model.Conversation
import com.sarath.gem.domain.model.Message
import com.sarath.gem.domain.model.StartUpPrompt
import com.sarath.gem.domain.repository.ChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ChatRepositoryImpl
@Inject
constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val promptDao: PromptDao,
    private val gemAIModel: GemAIModel,
    private val systemAIModel: SystemAIModel,
) : ChatRepository {

    override suspend fun createConversation(title: String): Result<Conversation, RequestError> {
        return suspendedSafeTry {
            val conversation =
                ConversationEntity(
                    timestamp = System.currentTimeMillis(),
                    title = title,
                    lastMessageTimestamp = System.currentTimeMillis(),
                )
            val id = conversationDao.insert(conversation)
            Result.Success(conversation.copy(id = id).toDomain())
        } ?: Result.Error(RequestError.Generic("Failed to create conversation"))
    }

    override fun getConversations(): Flow<List<Conversation>> {
        return safeTry { conversationDao.getConversations().map { it.map { it.toDomain() } } } ?: emptyFlow()
    }

    override suspend fun getConversationById(id: Long): Result<Conversation, RequestError> {
        TODO("Not yet implemented")
    }

    override fun getConversationMessages(conversationId: Long): Flow<List<Message>> {
        return safeTry { messageDao.getMessagesForConversation(conversationId).map { it.map { it.toDomain() } } }
            ?: emptyFlow()
    }

    override suspend fun sendMessage(message: Message): Result<Unit, RequestError> {
        // Insert the initial message
        val id =
            suspendedSafeTry { messageDao.addMessageToConversation(message.toEntity()) }
                ?: return Result.Error(RequestError.Generic("Failed to insert message"))

        // Temporary variable to store the id of the received message
        var receivedId: Long? = null

        return try {
            var responseStarted = false
            gemAIModel
                .sendMessage(conversationId = message.conversationId, content = message.content)
                .transform { data ->
                    if (!responseStarted) {
                        messageDao.updateMessageStatus(id, MessageStatus.SENT)
                        responseStarted = true
                        emit(data)
                    } else emit(data)
                }
                .onCompletion { cause -> cause?.let { logError("Content stream completed with error", it) } }
                .catch {
                    logError("Content stream failed", it)
                    messageDao.updateMessageStatus(id, MessageStatus.FAILED)
                }
                .collect { response ->
                    response.text?.let { text ->
                        handleFlowResponseText(
                            receivedId = receivedId,
                            text = text,
                            conversationId = message.conversationId,
                            onNewIdGenerated = { newId -> receivedId = newId },
                        )
                    }
                }

            Result.Success(Unit)
        } catch (exception: Exception) {
            logError("Failed to send message", exception)
            messageDao.updateMessageStatus(id, MessageStatus.FAILED)
            receivedId?.let { messageDao.updateMessageStatus(it, MessageStatus.FAILED) }
            Result.Error(RequestError.Generic("Failed"))
        }
    }

    override suspend fun getChatHistory(conversationId: Long): Result<List<Message>, RequestError> {
        return suspendedSafeTry {
            val messages = messageDao.getMessages(conversationId).map { it.toDomain() }
            Result.Success(messages)
        } ?: Result.Error(RequestError.Generic("Failed to get chat history"))
    }

    override suspend fun updateConversationTitle(conversationId: Long, title: String): Result<Unit, RequestError> {
        return suspendedSafeTry {
            val messageCount = messageDao.getMessageCount(conversationId)
            if (messageCount == 0) {
                conversationDao.updateTitle(conversationId = conversationId, title = title)
            }
            Result.Success(Unit)
        } ?: Result.Error(RequestError.Generic("Failed to update conversation title"))
    }

    override suspend fun updateChatTitle(conversationId: Long, prompt: String): Result<Unit, RequestError> =
        withContext(Dispatchers.IO) {
            suspendedSafeTry {
                // Generate chat title
                val title = async { systemAIModel.generateChatTitle(prompt) }

                // Get message count
                val messageCount = messageDao.getMessageCount(conversationId)

                if (messageCount <= 1) {
                    title.await()?.let {
                        Log.d("ChatRepositoryImpl", "updateChatTitle: $it")
                        conversationDao.updateTitle(conversationId = conversationId, title = it.trimEnd())
                        async { getPromptSuggestions() }
                    }
                }
                Log.d("ChatRepositoryImpl", "updateChatTitle Done")
                Result.Success(Unit)
            } ?: Result.Error(RequestError.Generic("Failed to update conversation title"))
        }

    override fun getPrompts(): Flow<List<StartUpPrompt>> {
        return safeTry {
            promptDao
                .getLastPromptsFlow(limit = 4)
                .map { list ->
                    list.shuffled().take(2) // Shuffle the last 4 items and take 2 random items
                }
                .map { list -> list.toDomain() }
        } ?: emptyFlow()
    }

    private suspend fun getPromptSuggestions() = coroutineScope {
        val convoIds = conversationDao.getConversationsForPromptSuggestions()
        val deferredPrompts =
            convoIds.map { convoId ->
                async {
                    val messages = messageDao.getParticipantMessages(convoId)
                    messages.map { it.participant.role to it.content }
                }
            }

        val prompts =
            deferredPrompts.awaitAll().flatten().map { (role, content) -> // here its only user role msgs
                "$role: $content"
            }

        val fetchedPrompts =
            try {
                systemAIModel.generateInitialPrompts(prompts.toString())
            } catch (e: Exception) {
                logError("Failed to fetch prompts", e)
                emptyList()
            }
        fetchedPrompts.map {
            async {
                promptDao.insert(PromptEntity(text = it.text, icon = it.icon, createdAt = System.currentTimeMillis()))
            }
        }
    }

    /**
     * Handles the text response received from a flow.
     *
     * This function inserts a new message into the database or updates an existing one, depending on whether a
     * `receivedId` is provided. If a new message is inserted, the `onNewIdGenerated` callback is invoked with the
     * generated ID.
     *
     * @param receivedId The ID of the message to update. If null, a new message is inserted.
     * @param text The text content of the response.
     * @param conversationId The ID of the conversation the message belongs to.
     * @param onNewIdGenerated A callback function that is invoked when a new message ID is generated.
     */
    private suspend fun handleFlowResponseText(
        receivedId: Long?,
        text: String,
        conversationId: Long,
        onNewIdGenerated: (Long) -> Unit,
    ) {
        messageDao.insertOrUpdate(
            id = receivedId,
            insert = {
                val newId =
                    messageDao.insert(Message.receive(conversationId = conversationId, content = text).toEntity())
                onNewIdGenerated(newId)
            },
            update = { updateId, content ->
                messageDao.upsert(
                    Message.receive(id = updateId, conversationId = conversationId, content = content + text).toEntity()
                )
            },
        )
    }

    private fun logError(message: String, exception: Throwable) {
        exception.printStackTrace()
        Log.e("ChatRepositoryImpl", "$message: ${exception.message}")
    }
}
