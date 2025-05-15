package com.sarath.gem.domain.use_case.chat

import com.sarath.gem.core.base.BaseUseCase
import com.sarath.gem.domain.RequestError
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.repository.ChatRepository
import javax.inject.Inject

class DeleteChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
):BaseUseCase<Long,Result<Unit, RequestError>> {

    override suspend fun perform(params: Long):  Result<Unit, RequestError> {
        return chatRepository.deleteChat(chatId = params)
    }
}