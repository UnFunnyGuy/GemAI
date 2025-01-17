package com.sarath.gem.presentation.screen.chat.viewmodel

import com.sarath.gem.core.base.UIAction

sealed interface ChatUIAction : UIAction {
    data class SetPrompt(val prompt: String) : ChatUIAction

    data class SetChatId(val chatId: Long) : ChatUIAction

    data object CreateNewChat : ChatUIAction

    data object Submit : ChatUIAction

    data object Clear : ChatUIAction
}
