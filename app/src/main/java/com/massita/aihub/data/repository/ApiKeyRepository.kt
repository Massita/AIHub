package com.massita.aihub.data.repository

import com.massita.aihub.data.model.AiProvider
import kotlinx.coroutines.flow.StateFlow

data class ApiKeyState(
    val statuses: Map<AiProvider, Boolean> = AiProvider.entries.associateWith { false }
) {
    val configuredCount: Int get() = statuses.count { it.value }
}

interface ApiKeyRepository {
    val state: StateFlow<ApiKeyState>
    fun saveApiKey(provider: AiProvider, apiKey: String)
    fun getApiKey(provider: AiProvider): String?
    fun removeApiKey(provider: AiProvider)
    fun hasApiKey(provider: AiProvider): Boolean
}
