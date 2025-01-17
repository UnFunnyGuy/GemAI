package com.sarath.gem.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sarath.gem.core.base.PromptIcon

@Entity(tableName = "prompts")
data class PromptEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val icon: PromptIcon = PromptIcon.QUESTION_MARK,
    val createdAt: Long = System.currentTimeMillis(),
)

val DefaultPrompts =
    listOf(
        PromptEntity(text = "Translate text to Spanish", icon = PromptIcon.TRANSLATION),
        PromptEntity(text = "Write a short poem about nature", icon = PromptIcon.LITERATURE),
        PromptEntity(text = "Create a Python function for sorting a list", icon = PromptIcon.CODE),
        PromptEntity(text = "Generate a script for a short play", icon = PromptIcon.LITERATURE),
    )
