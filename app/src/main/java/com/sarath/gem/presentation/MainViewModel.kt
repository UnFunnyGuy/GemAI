package com.sarath.gem.presentation

import androidx.lifecycle.viewModelScope
import com.sarath.gem.core.base.BaseViewModel
import com.sarath.gem.core.base.UIAction
import com.sarath.gem.core.base.UIEvent
import com.sarath.gem.core.base.UIState
import com.sarath.gem.domain.Result
import com.sarath.gem.domain.model.Message
import com.sarath.gem.domain.use_case.api_key.GetApiKeyUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val getApiKeyUseCase: GetApiKeyUseCase) :
    BaseViewModel<MainUIState, MainUIEvent, MainUIAction>() {

    override fun initialState(): MainUIState {
        return MainUIState(isApiKeySet = false, isCheckingApiKey = true)
    }

    override fun onActionEvent(action: MainUIAction) {}

    init {
        viewModelScope.launch {
            when (getApiKeyUseCase.perform()) {
                is Result.Success -> {
                    update { copy(isApiKeySet = true, isCheckingApiKey = false) }
                }
                is Result.Error -> {
                    update { copy(isApiKeySet = false, isCheckingApiKey = false) }
                }
            }
        }
    }
}

data class MainUIState(val isApiKeySet: Boolean, val isCheckingApiKey: Boolean) : UIState

sealed interface MainUIAction : UIAction

sealed interface MainUIEvent : UIEvent {
    data class ShowMessage(val message: Message) : MainUIEvent
}
