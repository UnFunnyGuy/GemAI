package com.sarath.gem.presentation.screen.chat.viewmodel

import com.sarath.gem.core.base.UIState
import com.sarath.gem.domain.model.Conversation
import com.sarath.gem.domain.model.Message
import com.sarath.gem.domain.model.StartUpPrompt

data class ChatUIState(
    val prompt: String,
    val chat: List<Message>,
    val chats: List<Conversation>,
    val isLoading: Boolean,
    val chatId: Long?,
    val startupPrompts: List<StartUpPrompt>,
) : UIState
