package com.sarath.gem.domain.model

data class UserConfig(val model: AIModel, val apiKey: String?, val hasApiKey: Boolean) {
    companion object {
        val DEFAULT = UserConfig(model = AIModel.GEMINI_1_5_FLASH, apiKey = null, hasApiKey = false)
    }
}

enum class AIModel(val modelName: String, val number: Int) {
    GEMINI_2_0_FLASH_EXP("gemini-2.0-flash-exp", 0),
    GEMINI_1_5_FLASH("gemini-1.5-flash", 1),
    GEMINI_1_5_PRO("gemini-1.5-pro-latest", 2);

    companion object {
        fun fromNum(id: Int?): AIModel {
            return when (id) {
                0 -> GEMINI_2_0_FLASH_EXP
                1 -> GEMINI_1_5_FLASH
                2 -> GEMINI_1_5_PRO
                else -> GEMINI_1_5_FLASH
            }
        }
    }
}
