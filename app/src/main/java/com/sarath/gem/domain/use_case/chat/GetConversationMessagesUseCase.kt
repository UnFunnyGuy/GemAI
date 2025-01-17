package com.sarath.gem.domain.use_case.chat

import com.sarath.gem.core.base.BaseUseCase
import com.sarath.gem.domain.model.Message
import com.sarath.gem.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * A use case responsible for retrieving messages for a specific conversation, as a stream of data.
 *
 * This class encapsulates the logic for fetching messages related to a chat conversation. It delegates the actual data
 * retrieval to a [ChatRepository] and exposes the results as a [Flow], allowing for real-time updates.
 *
 * @property chatRepository The repository for accessing chat-related data and operations.
 */
class GetConversationMessagesUseCase @Inject constructor(private val chatRepository: ChatRepository) :
    BaseUseCase<Long, List<Message>> {
    override fun performStreaming(params: Long): Flow<List<Message>> {
        return chatRepository.getConversationMessages(params)
    }
}
