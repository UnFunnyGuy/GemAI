package com.sarath.gem.presentation.screen.chat.viewmodel

import androidx.lifecycle.viewModelScope
import com.sarath.gem.core.base.BaseViewModel
import com.sarath.gem.domain.map
import com.sarath.gem.domain.model.Conversation
import com.sarath.gem.domain.model.Message
import com.sarath.gem.domain.onSuccess
import com.sarath.gem.domain.use_case.chat.CreateConversationUseCase
import com.sarath.gem.domain.use_case.chat.GetConversationMessagesUseCase
import com.sarath.gem.domain.use_case.chat.GetConversationsUseCase
import com.sarath.gem.domain.use_case.chat.GetPromptsUseCase
import com.sarath.gem.domain.use_case.chat.SendMessageUseCase
import com.sarath.gem.domain.use_case.chat.UpdateChatTitleParams
import com.sarath.gem.domain.use_case.chat.UpdateChatTitleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel
@Inject
constructor(
    private val createConversationUseCase: CreateConversationUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getConversationMessagesUseCase: GetConversationMessagesUseCase,
    private val getConversationsUseCase: GetConversationsUseCase,
    private val updateChatTitleUseCase: UpdateChatTitleUseCase,
    private val getPromptsUseCase: GetPromptsUseCase,
) : BaseViewModel<ChatUIState, ChatUIEvent, ChatUIAction>() {

    private val currentChatId: MutableStateFlow<Long?> = MutableStateFlow(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    private val messageFlow =
        currentChatId.flatMapLatest { id ->
            id?.let {
                update { copy(chatId = it) }
                getConversationMessagesUseCase.performStreaming(it)
            } ?: emptyFlow()
        }

    override fun initialState(): ChatUIState {
        return ChatUIState(
            prompt = "",
            chat = emptyList(),
            chats = emptyList(),
            isLoading = false,
            chatId = null,
            startupPrompts = emptyList(),
        )
    }

    init {

        viewModelScope.launch {
            messageFlow.catch { update { copy(isLoading = false) } }.collectLatest { update { copy(chat = it) } }
        }

        viewModelScope.launch {
            getConversationsUseCase.performStreaming().collectLatest { update { copy(chats = it) } }
        }

        viewModelScope.launch {
            getPromptsUseCase.performStreaming().collectLatest { update { copy(startupPrompts = it) } }
        }
    }

    override fun onActionEvent(action: ChatUIAction) {
        when (action) {
            ChatUIAction.Clear -> {}
            ChatUIAction.Submit -> submit()
            is ChatUIAction.SetPrompt -> update { copy(prompt = action.prompt) }
            is ChatUIAction.SetChatId -> switchChat(chatId = action.chatId)
            ChatUIAction.CreateNewChat -> createNewChat()
        }
    }

    private fun switchChat(chatId: Long) {
        if (currentState.chatId != chatId) {
            update { copy(chat = emptyList()) }
            currentChatId.value = chatId
        }
    }

    private fun createNewChat() {
        update { copy(chat = emptyList(), chatId = null) }
    }

    private fun submit() {
        if (uiState.value.prompt.isEmpty()) return
        val prompt = uiState.value.prompt
        update { copy(prompt = "", isLoading = true) }
        viewModelScope.launch {
            if (currentState.chatId == null) {
                createConversationUseCase.perform("New Chat").onSuccess { conversation: Conversation ->
                    currentChatId.value = conversation.id
                }
            }
            if (currentState.chat.isEmpty()) {
                async {
                    updateChatTitleUseCase.perform(
                        UpdateChatTitleParams(
                            conversationId =
                                currentChatId.value ?: throw IllegalStateException("Conversation ID can not be null"),
                            prompt = prompt,
                        )
                    )
                }
            }
            sendMessageUseCase
                .perform(
                    Message.send(
                        conversationId =
                            currentChatId.value ?: throw IllegalStateException("Conversation ID can not be null"),
                        content = prompt,
                    )
                )
                .map { update { copy(isLoading = false) } }
        }
    }
}
