package com.sarath.gem.domain.use_case.chat

import com.sarath.gem.core.base.BaseUseCase
import com.sarath.gem.domain.model.StartUpPrompt
import com.sarath.gem.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPromptsUseCase @Inject constructor(private val chatRepository: ChatRepository) :
    BaseUseCase<Unit, List<StartUpPrompt>> {

    override fun performStreaming(): Flow<List<StartUpPrompt>> {
        return chatRepository.getPrompts()
    }
}
