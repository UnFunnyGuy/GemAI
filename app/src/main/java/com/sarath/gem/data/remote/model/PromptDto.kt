package com.sarath.gem.data.remote.model

import com.google.ai.client.generativeai.type.FunctionType
import com.google.ai.client.generativeai.type.Schema
import com.sarath.gem.core.base.PromptIcon
import kotlinx.serialization.Serializable

@Serializable
data class PromptDto(val text: String, val icon: PromptIcon = PromptIcon.QUESTION_MARK) {
    companion object SCHEMA {

        val prompt =
            Schema(
                name = "prompt",
                description = "A Ai generated prompt for the user to use",
                type = FunctionType.OBJECT,
                properties =
                    mapOf(
                        "text" to
                            Schema(
                                name = "text",
                                description = "prompt text",
                                type = FunctionType.STRING,
                                nullable = false,
                            ),
                        "icon" to
                            Schema(
                                name = "icon",
                                description = "prompt icon",
                                type = FunctionType.STRING,
                                nullable = false,
                                enum = PromptIcon.entries.map { it.name },
                            ),
                    ),
                required = listOf("recipeName", "icon"),
            )

        val prompts =
            Schema(name = "prompts", description = "List of prompts", type = FunctionType.ARRAY, items = prompt)
    }
}
