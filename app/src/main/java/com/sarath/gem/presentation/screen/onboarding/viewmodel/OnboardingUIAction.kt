package com.sarath.gem.presentation.screen.onboarding.viewmodel

import com.sarath.gem.core.base.UIAction

sealed interface OnboardingUIAction : UIAction {
    data class SetApiKey(val apiKey: String) : OnboardingUIAction

    data object SaveApiKey : OnboardingUIAction

    data object GetApiKey : OnboardingUIAction
}
