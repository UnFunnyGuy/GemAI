package com.sarath.gem.domain.use_case.api_key

import com.sarath.gem.core.base.BaseUseCase
import com.sarath.gem.core.util.Dispatcher
import com.sarath.gem.core.util.GemAIDispatchers
import com.sarath.gem.domain.RequestError.ApiKeyError
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.repository.ApiKeyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A use case responsible for saving the API key to the data layer.
 *
 * This class encapsulates the logic for persisting an API key. It utilizes a [CoroutineDispatcher] for asynchronous
 * execution on a specified thread pool.
 *
 * @property apiKeyRepository The repository for accessing API key data.
 * @property dispatcher The coroutine dispatcher to use for asynchronous operation.
 */
class SaveApiKeyUseCase
@Inject
constructor(
    private val apiKeyRepository: ApiKeyRepository,
    @Dispatcher(GemAIDispatchers.IO) private val dispatcher: CoroutineDispatcher,
) : BaseUseCase<String, Result<Unit, ApiKeyError>> {
    override suspend fun perform(parms: String): Result<Unit, ApiKeyError> =
        withContext(dispatcher) { apiKeyRepository.saveApiKey(parms) }
}
