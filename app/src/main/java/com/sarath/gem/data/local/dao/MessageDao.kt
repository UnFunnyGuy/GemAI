package com.sarath.gem.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.sarath.gem.core.base.BaseDao
import com.sarath.gem.data.local.model.MessageEntity
import com.sarath.gem.data.local.model.MessageStatus
import com.sarath.gem.data.local.model.Participant
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao : BaseDao<MessageEntity> {

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId")
    fun getMessagesForConversation(conversationId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId")
    suspend fun getMessages(conversationId: Long): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND participant = :participant")
    suspend fun getParticipantMessages(
        conversationId: Long,
        participant: Participant = Participant.USER,
    ): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE id = :id") suspend fun getMessageById(id: Long?): MessageEntity?

    @Query("UPDATE messages SET status = :status WHERE id = :id")
    suspend fun updateMessageStatus(id: Long, status: MessageStatus)

    // Dont know where to put this
    @Query("UPDATE conversations SET lastMessageTimestamp = :timestamp WHERE id = :conversationId")
    suspend fun updateLastMessageTimestamp(conversationId: Long, timestamp: Long)

    @Transaction
    suspend fun addMessageToConversation(message: MessageEntity): Long {
        val id = insert(message)
        updateLastMessageTimestamp(message.conversationId, message.timestamp)
        return id
    }

    @Transaction
    suspend fun insertOrUpdate(
        id: Long?,
        insert: suspend () -> Unit,
        update: suspend (id: Long, messageContent: String) -> Unit,
    ) {
        if (id == null) {
            // Insert if id is null
            insert()
            return
        }

        val message = getMessageById(id)
        if (message == null) {
            // Insert if no message found for the id
            insert()
        } else {
            // Update with the existing message content
            update(id, message.content)
        }
    }

    @Query("SELECT COUNT(*) FROM messages WHERE conversationId = :conversationId")
    suspend fun getMessageCount(conversationId: Long): Int
}
