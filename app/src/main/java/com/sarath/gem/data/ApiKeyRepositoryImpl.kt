package com.sarath.gem.data

import com.sarath.gem.core.ai.GemAIModel
import com.sarath.gem.core.util.suspendedSafeTry
import com.sarath.gem.domain.RequestError
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.repository.ApiKeyRepository
import com.sarath.gem.domain.repository.DatastoreRepository
import javax.inject.Inject

class ApiKeyRepositoryImpl
@Inject
constructor(private val dataStoreRepository: DatastoreRepository, private val gemAIModel: GemAIModel) :
    ApiKeyRepository {

    override suspend fun saveApiKey(apiKey: String): Result<Unit, RequestError.ApiKeyError> {
        return suspendedSafeTry {
            val isValidKey = gemAIModel.testKey(apiKey)
            if (isValidKey) {
                dataStoreRepository.saveApiKey(apiKey)
                Result.Success(Unit)
            } else {
                Result.Error(RequestError.ApiKeyError)
            }
        } ?: Result.Error(RequestError.ApiKeyError)
    }

    override suspend fun getApiKey(): Result<String, RequestError.ApiKeyError> {
        return suspendedSafeTry {
            val key = dataStoreRepository.getApiKey()
            if (key.isNullOrBlank()) throw IllegalStateException("Api Key is not set")

            Result.Success(key)
        } ?: Result.Error(RequestError.ApiKeyError)
    }
}
