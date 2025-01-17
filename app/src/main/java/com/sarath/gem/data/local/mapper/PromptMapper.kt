package com.sarath.gem.data.local.mapper

import com.sarath.gem.core.base.BaseMapper
import com.sarath.gem.data.local.model.PromptEntity
import com.sarath.gem.domain.model.StartUpPrompt

object PromptMapper : BaseMapper<PromptEntity, StartUpPrompt> {
    override fun mapToDomain(entity: PromptEntity): StartUpPrompt {
        with(entity) {
            return StartUpPrompt(id = id, text = text, icon = icon, createdAt = createdAt)
        }
    }

    override fun mapToEntity(domain: StartUpPrompt): PromptEntity {
        with(domain) {
            return PromptEntity(id = id, text = text, icon = icon, createdAt = createdAt)
        }
    }
}

fun List<PromptEntity>.toDomain(): List<StartUpPrompt> {
    return map { PromptMapper.mapToDomain(it) }
}
