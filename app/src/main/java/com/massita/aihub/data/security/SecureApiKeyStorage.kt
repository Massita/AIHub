package com.massita.aihub.data.security

import com.massita.aihub.data.model.AiProvider

interface SecureApiKeyStorage {
    fun save(provider: AiProvider, apiKey: String)
    fun get(provider: AiProvider): String?
    fun remove(provider: AiProvider)
    fun has(provider: AiProvider): Boolean
    fun configuredProviders(): Set<AiProvider>
    fun clearAll()
}
