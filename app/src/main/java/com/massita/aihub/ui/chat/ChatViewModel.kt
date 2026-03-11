package com.massita.aihub.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.massita.aihub.data.model.AiProvider
import com.massita.aihub.data.repository.ApiKeyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val selectedProvider: AiProvider? = null,
    val configuredProviders: List<AiProvider> = emptyList(),
    val isModelSelectorExpanded: Boolean = false
) {
    val hasConfiguredModels: Boolean get() = configuredProviders.isNotEmpty()
}

class ChatViewModel(
    private val apiKeyRepository: ApiKeyRepository
) : ViewModel() {

    private val _localState = MutableStateFlow(LocalChatState())

    val uiState: StateFlow<ChatUiState> = combine(
        _localState,
        apiKeyRepository.state
    ) { local, apiKeyState ->
        val configured = AiProvider.entries.filter { apiKeyState.statuses[it] == true }
        val selected = when {
            local.selectedProvider != null && local.selectedProvider in configured -> local.selectedProvider
            configured.isNotEmpty() -> configured.first()
            else -> null
        }
        ChatUiState(
            messages = local.messages,
            inputText = local.inputText,
            selectedProvider = selected,
            configuredProviders = configured,
            isModelSelectorExpanded = local.isModelSelectorExpanded
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ChatUiState())

    fun onInputChanged(text: String) {
        _localState.update { it.copy(inputText = text) }
    }

    fun onSendMessage() {
        val text = _localState.value.inputText.trim()
        if (text.isBlank()) return

        val userMessage = ChatMessage(content = text, isUser = true)
        _localState.update {
            it.copy(
                messages = it.messages + userMessage,
                inputText = ""
            )
        }
    }

    fun onProviderSelected(provider: AiProvider) {
        _localState.update {
            it.copy(selectedProvider = provider, isModelSelectorExpanded = false)
        }
    }

    fun onToggleModelSelector() {
        _localState.update { it.copy(isModelSelectorExpanded = !it.isModelSelectorExpanded) }
    }

    fun onDismissModelSelector() {
        _localState.update { it.copy(isModelSelectorExpanded = false) }
    }

    private data class LocalChatState(
        val messages: List<ChatMessage> = emptyList(),
        val inputText: String = "",
        val selectedProvider: AiProvider? = null,
        val isModelSelectorExpanded: Boolean = false
    )
}
