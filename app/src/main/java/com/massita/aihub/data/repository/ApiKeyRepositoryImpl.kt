package com.massita.aihub.data.repository

import com.massita.aihub.data.model.AiProvider
import com.massita.aihub.data.security.SecureApiKeyStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ApiKeyRepositoryImpl(
    private val storage: SecureApiKeyStorage
) : ApiKeyRepository {

    private val _state = MutableStateFlow(buildState())
    override val state: StateFlow<ApiKeyState> = _state.asStateFlow()

    override fun saveApiKey(provider: AiProvider, apiKey: String) {
        storage.save(provider, apiKey)
        refreshState()
    }

    override fun getApiKey(provider: AiProvider): String? = storage.get(provider)

    override fun removeApiKey(provider: AiProvider) {
        storage.remove(provider)
        refreshState()
    }

    override fun hasApiKey(provider: AiProvider): Boolean = storage.has(provider)

    private fun refreshState() {
        _state.update { buildState() }
    }

    private fun buildState() = ApiKeyState(
        statuses = AiProvider.entries.associateWith { storage.has(it) }
    )
}
