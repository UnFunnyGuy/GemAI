package com.sarath.gem.presentation.screen.chat.viewmodel

import com.sarath.gem.core.base.UIEvent

sealed interface ChatUIEvent : UIEvent {
    data class Error(val message: String) : ChatUIEvent
}
