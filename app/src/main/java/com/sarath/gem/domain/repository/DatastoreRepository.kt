package com.sarath.gem.domain.repository

import com.sarath.gem.domain.model.AIModel
import com.sarath.gem.domain.model.UserConfig
import kotlinx.coroutines.flow.Flow

interface DatastoreRepository {
    val userConfigFlow: Flow<UserConfig>

    suspend fun getModel(): AIModel

    suspend fun getApiKey(): String?

    suspend fun saveApiKey(apiKey: String)

    suspend fun saveModel(model: AIModel)
}
