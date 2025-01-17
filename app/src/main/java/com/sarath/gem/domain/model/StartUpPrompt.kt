package com.sarath.gem.domain.model

import com.sarath.gem.core.base.PromptIcon

data class StartUpPrompt(
    val id: Long = 0,
    val text: String,
    val icon: PromptIcon = PromptIcon.QUESTION_MARK,
    val createdAt: Long = System.currentTimeMillis(),
)
