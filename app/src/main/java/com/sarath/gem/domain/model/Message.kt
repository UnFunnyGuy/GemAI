package com.sarath.gem.domain.model

import com.sarath.gem.data.local.model.MessageStatus
import com.sarath.gem.data.local.model.Participant

data class Message(
    val id: Long = 0,
    val conversationId: Long,
    val timestamp: Long,
    val content: String,
    val participant: Participant,
    val status: MessageStatus,
) {
    companion object {
        fun send(conversationId: Long, content: String): Message =
            Message(
                conversationId = conversationId,
                timestamp = System.currentTimeMillis(),
                content = content,
                participant = Participant.USER,
                status = MessageStatus.LOADING,
            )

        fun receive(conversationId: Long, content: String): Message =
            Message(
                conversationId = conversationId,
                timestamp = System.currentTimeMillis(),
                content = content,
                participant = Participant.MODEL,
                status = MessageStatus.RECEIVED,
            )

        fun receive(id: Long = 0, conversationId: Long, content: String): Message =
            Message(
                id = id,
                conversationId = conversationId,
                timestamp = System.currentTimeMillis(),
                content = content,
                participant = Participant.MODEL,
                status = MessageStatus.RECEIVED,
            )
    }

    fun update(content: String = this.content, status: MessageStatus = this.status) =
        this.copy(content = content, status = status)
}
