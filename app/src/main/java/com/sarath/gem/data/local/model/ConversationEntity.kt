package com.sarath.gem.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val title: String?,
    val lastMessageTimestamp: Long,
    val isUsedForPromptSuggestions: Boolean = false,
)
