package com.sarath.gem.data.local.mapper

import com.sarath.gem.core.base.BaseMapper
import com.sarath.gem.data.local.model.ConversationEntity
import com.sarath.gem.domain.model.Conversation

object ConversationMapper : BaseMapper<ConversationEntity, Conversation> {
    override fun mapToDomain(entity: ConversationEntity): Conversation {
        return with(entity) {
            Conversation(id = id, timestamp = timestamp, title = title, lastMessageTimestamp = lastMessageTimestamp)
        }
    }

    override fun mapToEntity(domain: Conversation): ConversationEntity {
        return with(domain) {
            ConversationEntity(
                id = id,
                timestamp = timestamp,
                title = title,
                lastMessageTimestamp = lastMessageTimestamp,
            )
        }
    }
}

fun Conversation.toEntity() = ConversationMapper.mapToEntity(this)

fun ConversationEntity.toDomain() = ConversationMapper.mapToDomain(this)
