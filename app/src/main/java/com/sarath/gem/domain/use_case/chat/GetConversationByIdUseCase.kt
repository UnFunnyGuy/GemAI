package com.sarath.gem.domain.use_case.chat

import com.sarath.gem.core.base.BaseUseCase
import com.sarath.gem.domain.RequestError
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.model.Conversation
import com.sarath.gem.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * A use case responsible for retrieving a conversation by its unique ID.
 *
 * This class encapsulates the logic for fetching a specific chat conversation based on its ID, delegating the actual
 * data retrieval process to a [ChatRepository].
 *
 * @property chatRepository The repository for accessing chat-related data and operations.
 */
class GetConversationByIdUseCase @Inject constructor(private val chatRepository: ChatRepository) :
    BaseUseCase<Long, Result<Conversation, RequestError>> {
    override suspend fun perform(parms: Long): Result<Conversation, RequestError> {
        return chatRepository.getConversationById(parms)
    }
}
