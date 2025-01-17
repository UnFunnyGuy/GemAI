package com.sarath.gem.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.sarath.gem.core.base.BaseDao
import com.sarath.gem.data.local.model.PromptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PromptDao : BaseDao<PromptEntity> {

    @Query("SELECT * FROM prompts") suspend fun getPrompts(): List<PromptEntity>

    @Query("SELECT * FROM prompts ORDER BY createdAt DESC LIMIT 3")
    suspend fun getLatestThreePrompts(): List<PromptEntity>

    @Query("SELECT * FROM prompts") fun getPromptsFlow(): Flow<List<PromptEntity>>

    @Query("SELECT * FROM prompts ORDER BY createdAt DESC LIMIT :limit")
    fun getLastPromptsFlow(limit: Int): Flow<List<PromptEntity>>
}
