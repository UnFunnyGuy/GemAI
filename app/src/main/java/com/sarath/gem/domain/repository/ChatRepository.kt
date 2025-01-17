package com.sarath.gem.domain.repository

import com.sarath.gem.domain.RequestError
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.model.Conversation
import com.sarath.gem.domain.model.Message
import com.sarath.gem.domain.model.StartUpPrompt
import kotlinx.coroutines.flow.Flow

// Could use UseCase in future, for now its too much this simple project[just wanted to finish this
// asap]
interface ChatRepository {

    suspend fun createConversation(title: String): Result<Conversation, RequestError>

    fun getConversations(): Flow<List<Conversation>>

    suspend fun getConversationById(id: Long): Result<Conversation, RequestError>

    fun getConversationMessages(conversationId: Long): Flow<List<Message>>

    suspend fun sendMessage(message: Message): Result<Unit, RequestError>

    suspend fun getChatHistory(conversationId: Long): Result<List<Message>, RequestError>

    suspend fun updateConversationTitle(conversationId: Long, title: String): Result<Unit, RequestError>

    /**
     * Updates the title of a chat conversation using AI to generate a title based on the first prompt.
     *
     * This function takes the initial [prompt] of a conversation identified by [conversationId], and uses an AI model
     * to generate a more descriptive title. It then updates the conversation's title.
     *
     * @param conversationId The unique identifier of the chat conversation to update.
     * @param prompt The first prompt of the conversation. This is used as input for AI title generation.
     * @return A [Result] indicating the success or failure of the update operation.
     *     - `Result.Success(Unit)`: If the title was successfully updated.
     *     - `Result.Error(RequestError)`: If an error occurred during the update process, including errors from title
     *       generation.
     *
     * @see RequestError For possible errors that might be returned.
     */
    suspend fun updateChatTitle(conversationId: Long, prompt: String): Result<Unit, RequestError>

    fun getPrompts(): Flow<List<StartUpPrompt>>
}
