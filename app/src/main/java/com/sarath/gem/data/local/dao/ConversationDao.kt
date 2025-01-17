package com.sarath.gem.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.sarath.gem.core.base.BaseDao
import com.sarath.gem.data.local.model.ConversationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ConversationDao : BaseDao<ConversationEntity> {

    @Query("SELECT * FROM conversations") fun getConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id") suspend fun getConversationById(id: Long): ConversationEntity

    @Query("UPDATE conversations SET title = :title WHERE id = :conversationId")
    suspend fun updateTitle(conversationId: Long, title: String)

    @Query("UPDATE conversations SET isUsedForPromptSuggestions = :isUsed WHERE id = :conversationId")
    suspend fun setUsedForPromptSuggestions(conversationId: Long, isUsed: Boolean)

    @Query("SELECT conversations.id FROM conversations WHERE isUsedForPromptSuggestions = 0")
    suspend fun getConversationsForPromptSuggestions(): List<Long>
}
