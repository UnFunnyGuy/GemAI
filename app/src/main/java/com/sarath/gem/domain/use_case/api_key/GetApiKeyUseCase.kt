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
 * A use case responsible for retrieving the API key from the data layer.
 *
 * This class encapsulates the logic for fetching an API key, handling potential errors, and providing a result that
 * indicates success or failure. It utilizes a [CoroutineDispatcher] for asynchronous execution on a specified thread
 * pool.
 *
 * @property apiKeyRepository The repository for accessing API key data.
 * @property dispatcher The coroutine dispatcher to use for asynchronous operation.
 */
class GetApiKeyUseCase
@Inject
constructor(
    private val apiKeyRepository: ApiKeyRepository,
    @Dispatcher(GemAIDispatchers.IO) private val dispatcher: CoroutineDispatcher,
) : BaseUseCase<Unit, Result<String, ApiKeyError>> {
    override suspend fun perform(): Result<String, ApiKeyError> =
        withContext(dispatcher) { apiKeyRepository.getApiKey() }
}
