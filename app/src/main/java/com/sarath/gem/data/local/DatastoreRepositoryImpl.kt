package com.sarath.gem.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import androidx.datastore.dataStore
import com.sarath.gem.UserConfig
import com.sarath.gem.domain.model.AIModel
import com.sarath.gem.domain.repository.DatastoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.userConfigStore: DataStore<UserConfig> by
    dataStore(fileName = "UserConfig.pb", serializer = UserConfigSerializer())

class DatastoreRepositoryImpl @Inject constructor(@ApplicationContext context: Context) : DatastoreRepository {

    val dataStore = context.userConfigStore
    override val userConfigFlow: Flow<com.sarath.gem.domain.model.UserConfig> =
        dataStore.data
            .map {
                com.sarath.gem.domain.model.UserConfig(
                    model = AIModel.fromNum(it.model.number),
                    apiKey = it.apiKey,
                    hasApiKey = it.hasApiKey,
                )
            }
            .catch { exception ->
                // dataStore.data throws an IOException when an error is encountered when reading
                // data
                if (exception is IOException) {
                    emit(com.sarath.gem.domain.model.UserConfig.DEFAULT)
                } else {
                    throw exception
                }
            }

    override suspend fun getModel(): AIModel {
        return AIModel.fromNum(dataStore.data.firstOrNull()?.model?.number)
    }

    override suspend fun getApiKey(): String? {
        return dataStore.data.firstOrNull()?.apiKey
    }

    override suspend fun saveApiKey(apiKey: String) {
        dataStore.updateData { config -> config.toBuilder().setApiKey(apiKey).build() }
    }

    override suspend fun saveModel(model: AIModel) {
        dataStore.updateData { config ->
            config.toBuilder().setModel(com.sarath.gem.AIModel.forNumber(model.number)).build()
        }
    }
}
