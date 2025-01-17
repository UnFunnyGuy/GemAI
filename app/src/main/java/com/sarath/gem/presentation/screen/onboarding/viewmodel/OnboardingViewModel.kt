package com.sarath.gem.presentation.screen.onboarding.viewmodel

import androidx.lifecycle.viewModelScope
import com.sarath.gem.core.base.BaseViewModel
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.use_case.api_key.SaveApiKeyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(private val saveApiKeyUseCase: SaveApiKeyUseCase) :
    BaseViewModel<OnboardingUIState, OnboardingUIEvent, OnboardingUIAction>() {

    override fun initialState(): OnboardingUIState {
        return OnboardingUIState(apiKey = "", apiKeyState = ApiKeyCheckStatus.NONE)
    }

    override fun onActionEvent(action: OnboardingUIAction) {
        when (action) {
            is OnboardingUIAction.SetApiKey -> {
                if (!currentState.apiKeyState.isNone) {
                    update { copy(apiKeyState = ApiKeyCheckStatus.NONE) }
                }
                update { copy(apiKey = action.apiKey) }
            }
            OnboardingUIAction.SaveApiKey -> saveApiKey()
            OnboardingUIAction.GetApiKey -> sendOneTimeUIEvent(OnboardingUIEvent.GetApiKey)
        }
    }

    private fun saveApiKey() {
        if (currentState.apiKey.isEmpty() || currentState.apiKey.length < 12) {
            sendOneTimeUIEvent(OnboardingUIEvent.ShowToast("API Key cannot be empty"))
            return
        }
        update { copy(apiKeyState = ApiKeyCheckStatus.CHECKING) }
        viewModelScope.launch {
            val result = saveApiKeyUseCase.perform(currentState.apiKey)
            if (result is Result.Success) {
                update { copy(apiKeyState = ApiKeyCheckStatus.VALID) }
                sendOneTimeUIEvent(OnboardingUIEvent.NavigateToChatScreen, 500)
            } else {
                update { copy(apiKeyState = ApiKeyCheckStatus.INVALID) }
                sendOneTimeUIEvent(OnboardingUIEvent.ShowToast("Enter valid API Key"))
            }
        }
    }
}
