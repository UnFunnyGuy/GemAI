package com.sarath.gem.domain.use_case.chat

import com.sarath.gem.core.base.BaseUseCase
import com.sarath.gem.domain.model.Conversation
import com.sarath.gem.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for retrieving a list of conversations.
 *
 * This use case fetches all available conversations from the chat repository.
 *
 * @param chatRepository Repository for accessing and managing chat data.
 * @return A Flow emitting a list of Conversations.
 */
class GetConversationsUseCase @Inject constructor(private val chatRepository: ChatRepository) :
    BaseUseCase<Unit, List<Conversation>> {
    override fun performStreaming(): Flow<List<Conversation>> {
        return chatRepository.getConversations()
    }
}
