package com.sarath.gem.presentation.screen.onboarding.viewmodel

import com.sarath.gem.core.base.UIState

data class OnboardingUIState(val apiKey: String, val apiKeyState: ApiKeyCheckStatus) : UIState

enum class ApiKeyCheckStatus {
    VALID,
    INVALID,
    CHECKING,
    NONE;

    val isNone: Boolean
        get() = this == NONE

    val isNotLoading: Boolean
        get() = this != CHECKING
}
