package com.sarath.gem.domain.use_case.chat

import com.sarath.gem.core.base.BaseUseCase
import com.sarath.gem.core.util.Dispatcher
import com.sarath.gem.core.util.GemAIDispatchers
import com.sarath.gem.domain.RequestError
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.repository.ChatRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class UpdateChatTitleParams(val prompt: String, val conversationId: Long)

/**
 * Use case for updating the title of a chat conversation.
 *
 * This class encapsulates the logic for updating a chat title using the provided [ChatRepository]. It performs the
 * update operation on a background thread using the specified [CoroutineDispatcher].
 *
 * @property chatRepository The repository responsible for managing chat data.
 * @property dispatcher The coroutine dispatcher to use for performing the update operation on a background thread.
 */
class UpdateChatTitleUseCase
@Inject
constructor(
    private val chatRepository: ChatRepository,
    @Dispatcher(GemAIDispatchers.IO) private val dispatcher: CoroutineDispatcher,
) : BaseUseCase<UpdateChatTitleParams, Result<Unit, RequestError>> {
    override suspend fun perform(params: UpdateChatTitleParams): Result<Unit, RequestError> =
        withContext(dispatcher) {
            chatRepository.updateChatTitle(conversationId = params.conversationId, prompt = params.prompt)
        }
}
