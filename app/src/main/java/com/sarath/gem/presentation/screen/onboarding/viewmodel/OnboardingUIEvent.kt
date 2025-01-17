package com.sarath.gem.presentation.screen.onboarding.viewmodel

import com.sarath.gem.core.base.UIEvent

sealed interface OnboardingUIEvent : UIEvent {
    data object NavigateToChatScreen : OnboardingUIEvent

    data object GetApiKey : OnboardingUIEvent

    data class ShowToast(val message: String) : OnboardingUIEvent
}
