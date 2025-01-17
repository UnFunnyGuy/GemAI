package com.sarath.gem.domain.repository

import com.sarath.gem.domain.RequestError.ApiKeyError
import com.sarath.gem.domain.Result

interface ApiKeyRepository {
    suspend fun saveApiKey(apiKey: String): Result<Unit, ApiKeyError>

    suspend fun getApiKey(): Result<String, ApiKeyError>
}
