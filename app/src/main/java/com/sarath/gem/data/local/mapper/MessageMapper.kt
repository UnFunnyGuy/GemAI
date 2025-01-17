package com.sarath.gem.data.local.mapper

import com.sarath.gem.core.base.BaseMapper
import com.sarath.gem.data.local.model.MessageEntity
import com.sarath.gem.domain.model.Message

object MessageMapper : BaseMapper<MessageEntity, Message> {
    override fun mapToDomain(entity: MessageEntity): Message {
        return with(entity) {
            Message(
                id = id,
                conversationId = conversationId,
                timestamp = timestamp,
                content = content,
                participant = participant,
                status = status,
            )
        }
    }

    override fun mapToEntity(domain: Message): MessageEntity {
        return with(domain) {
            MessageEntity(
                id = id,
                conversationId = conversationId,
                timestamp = timestamp,
                content = content,
                participant = participant,
                status = status,
            )
        }
    }
}

fun Message.toEntity() = MessageMapper.mapToEntity(this)

fun MessageEntity.toDomain() = MessageMapper.mapToDomain(this)
