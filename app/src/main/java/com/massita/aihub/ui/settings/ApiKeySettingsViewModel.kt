package com.massita.aihub.ui.settings

import androidx.lifecycle.ViewModel
import com.massita.aihub.data.model.AiProvider
import com.massita.aihub.data.repository.ApiKeyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProviderField(
    val provider: AiProvider,
    val draft: String = "",
    val saved: Boolean = false,
    val revealed: Boolean = false
)

data class ApiKeySettingsUiState(
    val fields: List<ProviderField> = emptyList(),
    val snackbar: String? = null
)

class ApiKeySettingsViewModel(
    private val repository: ApiKeyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ApiKeySettingsUiState())
    val uiState: StateFlow<ApiKeySettingsUiState> = _uiState.asStateFlow()

    init {
        refreshFields()
    }

    fun onDraftChanged(provider: AiProvider, text: String) {
        _uiState.update { state ->
            state.copy(
                fields = state.fields.map {
                    if (it.provider == provider) it.copy(draft = text) else it
                }
            )
        }
    }

    fun onSave(provider: AiProvider) {
        val draft = _uiState.value.fields.firstOrNull { it.provider == provider }?.draft.orEmpty()
        if (draft.isBlank()) return

        repository.saveApiKey(provider, draft.trim())
        refreshFields()
        _uiState.update { it.copy(snackbar = "${provider.displayName} key saved") }
    }

    fun onRemove(provider: AiProvider) {
        repository.removeApiKey(provider)
        refreshFields()
        _uiState.update { it.copy(snackbar = "${provider.displayName} key removed") }
    }

    fun onToggleReveal(provider: AiProvider) {
        _uiState.update { state ->
            state.copy(
                fields = state.fields.map {
                    if (it.provider == provider) it.copy(revealed = !it.revealed) else it
                }
            )
        }
    }

    fun onSnackbarDismissed() {
        _uiState.update { it.copy(snackbar = null) }
    }

    private fun refreshFields() {
        _uiState.update { state ->
            state.copy(
                fields = AiProvider.entries.map { provider ->
                    val existing = state.fields.firstOrNull { it.provider == provider }
                    val hasSaved = repository.hasApiKey(provider)
                    ProviderField(
                        provider = provider,
                        draft = if (hasSaved) maskedKey(repository.getApiKey(provider)) else (existing?.draft ?: ""),
                        saved = hasSaved,
                        revealed = false
                    )
                }
            )
        }
    }

    private fun maskedKey(key: String?): String {
        if (key == null || key.length < 8) return "••••••••"
        return key.take(4) + "••••" + key.takeLast(4)
    }
}
