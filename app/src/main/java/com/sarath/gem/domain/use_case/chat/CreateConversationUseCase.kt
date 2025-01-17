package com.sarath.gem.domain.use_case.chat

import com.sarath.gem.core.base.BaseUseCase
import com.sarath.gem.domain.RequestError
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.model.Conversation
import com.sarath.gem.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * A use case responsible for creating a new conversation.
 *
 * This class encapsulates the logic for initiating a new chat conversation, delegating the actual creation process to a
 * [ChatRepository].
 *
 * @property chatRepository The repository for accessing chat-related data and operations.
 */
class CreateConversationUseCase @Inject constructor(private val chatRepository: ChatRepository) :
    BaseUseCase<String, Result<Conversation, RequestError>> {
    override suspend fun perform(parms: String): Result<Conversation, RequestError> {
        return chatRepository.createConversation(parms)
    }
}
