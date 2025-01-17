package com.sarath.gem.core.ai

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import com.sarath.gem.data.local.dao.MessageDao
import com.sarath.gem.domain.repository.DatastoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * A class that manages communication with the Gemini AI model, handling chat initialization, message sending, and
 * maintaining chat state. It uses a [DatastoreRepository] for settings and a [MessageDao] for managing message history.
 *
 * @property datastoreRepository Repository for accessing application datastore.
 * @property messageDao Data access object for messages.
 */
class GemAIModel @Inject constructor(datastoreRepository: DatastoreRepository, private val messageDao: MessageDao) :
    BaseAIModel(datastoreRepository) {

    private val chatLock = Mutex()

    private var chat: Chat? = null

    private var activeChatId: Long? = null

    /**
     * Sends a message to the Gemini AI model within a specific conversation and retrieves the response as a stream of
     * [GenerateContentResponse] objects.
     *
     * This function ensures that a chat is initialized for the given `conversationId` before sending the message. If a
     * chat is already active for a different `conversationId`, it will be re-initialized.
     *
     * The message content is sent to the Gemini AI model and the function returns a [Flow] that emits
     * [GenerateContentResponse] objects as they are received from the model. This allows for processing the response
     * incrementally.
     *
     * @param conversationId The unique identifier of the conversation to send the message to. Must be a positive value.
     * @param content The textual message to send to the Gemini AI model. Cannot be blank or contain only whitespace.
     * @return A [Flow] emitting [GenerateContentResponse] objects representing the AI's response stream.
     * @throws IllegalArgumentException If the `conversationId` is not a positive value or if the `content` is blank.
     * @throws IllegalStateException If the chat object is unexpectedly null after initialization.
     */
    suspend fun sendMessage(conversationId: Long, content: String): Flow<GenerateContentResponse> {
        require(conversationId > 0) { "Invalid conversation ID" }
        require(content.isNotBlank()) { "Message content cannot be blank" }
        val activeChat =
            chatLock.withLock {
                if (conversationId != activeChatId || chat == null) {
                    initializeChat(conversationId)
                }
                chat ?: throw IllegalStateException("Chat not initialized")
            }

        return activeChat.sendMessageStream(content)
    }

    /**
     * Initializes a new chat session with the given conversation ID and loads message history.
     *
     * This function retrieves the message history associated with the provided `conversationId` from the database. It
     * then transforms these messages into a format suitable for the Gemini AI model and starts a new chat session using
     * this history. If an error occurs during history retrieval, an empty list is used, effectively starting a new chat
     * without previous messages.
     *
     * @param conversationId The ID of the conversation to initialize.
     */
    private suspend fun initializeChat(conversationId: Long) {
        val history =
            try {
                messageDao.getMessages(conversationId).map { content(role = it.participant.role) { text(it.content) } }
            } catch (e: Exception) {
                emptyList()
            }
        setChat(geminiAIModel.startChat(history), conversationId)
    }

    /**
     * Sets the active chat session and updates the active conversation ID.
     *
     * @param newChat The new [Chat] instance.
     * @param conversationId The conversation ID associated with the chat.
     */
    private fun setChat(newChat: Chat, conversationId: Long) {
        activeChatId = conversationId
        chat = newChat
    }
}
