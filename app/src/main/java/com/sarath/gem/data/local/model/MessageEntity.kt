package com.sarath.gem.data.local.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys =
        [
            ForeignKey(
                entity = ConversationEntity::class,
                parentColumns = ["id"],
                childColumns = ["conversationId"],
                onDelete = ForeignKey.CASCADE,
            )
        ],
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val timestamp: Long,
    val content: String,
    val participant: Participant,
    val status: MessageStatus,
)

enum class Participant {
    USER,
    MODEL;

    val role: String
        get() =
            when (this) {
                USER -> "user"
                MODEL -> "model"
            }
}

enum class MessageStatus {
    LOADING,
    SENT,
    FAILED,
    RECEIVED,
}
