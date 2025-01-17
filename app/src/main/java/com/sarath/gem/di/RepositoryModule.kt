package com.sarath.gem.di

import com.sarath.gem.data.ApiKeyRepositoryImpl
import com.sarath.gem.data.ChatRepositoryImpl
import com.sarath.gem.data.local.DatastoreRepositoryImpl
import com.sarath.gem.domain.repository.ApiKeyRepository
import com.sarath.gem.domain.repository.ChatRepository
import com.sarath.gem.domain.repository.DatastoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds abstract fun bindDatastoreRepository(impl: DatastoreRepositoryImpl): DatastoreRepository

    @Binds abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

    @Binds abstract fun bindApiKeyRepository(impl: ApiKeyRepositoryImpl): ApiKeyRepository
}
