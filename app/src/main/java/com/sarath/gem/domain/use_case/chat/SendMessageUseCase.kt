package com.sarath.gem.domain.use_case.chat

import com.sarath.gem.core.base.BaseUseCase
import com.sarath.gem.core.util.Dispatcher
import com.sarath.gem.core.util.GemAIDispatchers
import com.sarath.gem.domain.RequestError
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.model.Message
import com.sarath.gem.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A use case responsible for sending a new message.
 *
 * This class encapsulates the logic for sending a message to a chat conversation. It delegates the actual sending
 * operation to a [ChatRepository] and utilizes a [CoroutineDispatcher] for performing the action on a background
 * thread.
 *
 * @property chatRepository The repository for accessing chat-related data and operations.
 * @property dispatcher The coroutine dispatcher to use for asynchronous operation.
 */
class SendMessageUseCase
@Inject
constructor(
    private val chatRepository: ChatRepository,
    @Dispatcher(GemAIDispatchers.IO) private val dispatcher: CoroutineDispatcher,
) : BaseUseCase<Message, Result<Unit, RequestError>> {
    override suspend fun perform(params: Message): Result<Unit, RequestError> =
        withContext(dispatcher) { chatRepository.sendMessage(params) }
}
