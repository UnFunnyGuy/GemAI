package com.sarath.gem.di

import android.content.Context
import com.sarath.gem.data.local.AppDatabase
import com.sarath.gem.data.local.dao.ConversationDao
import com.sarath.gem.data.local.dao.MessageDao
import com.sarath.gem.data.local.dao.PromptDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun provideLogDao(database: AppDatabase): ConversationDao {
        return database.conversationDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(database: AppDatabase): MessageDao {
        return database.messageDao()
    }

    @Provides
    @Singleton
    fun providePromptDao(database: AppDatabase): PromptDao {
        return database.promptDao()
    }

    @Provides
    @Singleton
    fun providesAppDatabase(@ApplicationContext context: Context): AppDatabase = AppDatabase.getInstance(context)
}
