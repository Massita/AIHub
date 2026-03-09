package com.massita.aihub.data.repository

import com.massita.aihub.data.model.AiProvider
import com.massita.aihub.data.security.SecureApiKeyStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ApiKeyRepositoryImplTest {

    private lateinit var fakeStorage: FakeSecureApiKeyStorage
    private lateinit var repository: ApiKeyRepositoryImpl

    @Before
    fun setUp() {
        fakeStorage = FakeSecureApiKeyStorage()
        repository = ApiKeyRepositoryImpl(fakeStorage)
    }

    @Test
    fun `initial state has no configured providers`() {
        val state = repository.state.value
        assertTrue(state.statuses.values.all { !it })
        assertEquals(0, state.configuredCount)
    }

    @Test
    fun `saveApiKey persists and updates state`() {
        repository.saveApiKey(AiProvider.OPENAI, "sk-test-key")

        assertTrue(repository.hasApiKey(AiProvider.OPENAI))
        assertEquals("sk-test-key", repository.getApiKey(AiProvider.OPENAI))
        assertTrue(repository.state.value.statuses[AiProvider.OPENAI] == true)
        assertEquals(1, repository.state.value.configuredCount)
    }

    @Test
    fun `removeApiKey clears and updates state`() {
        repository.saveApiKey(AiProvider.GEMINI, "AIza-test")
        repository.removeApiKey(AiProvider.GEMINI)

        assertFalse(repository.hasApiKey(AiProvider.GEMINI))
        assertNull(repository.getApiKey(AiProvider.GEMINI))
        assertEquals(0, repository.state.value.configuredCount)
    }

    @Test
    fun `multiple providers tracked independently`() {
        repository.saveApiKey(AiProvider.OPENAI, "sk-openai")
        repository.saveApiKey(AiProvider.ALIBABA, "sk-alibaba")

        val state = repository.state.value
        assertTrue(state.statuses[AiProvider.OPENAI] == true)
        assertTrue(state.statuses[AiProvider.ALIBABA] == true)
        assertFalse(state.statuses[AiProvider.GEMINI] == true)
        assertEquals(2, state.configuredCount)
    }

    @Test
    fun `getApiKey returns null for unconfigured provider`() {
        assertNull(repository.getApiKey(AiProvider.OPENAI))
    }
}

private class FakeSecureApiKeyStorage : SecureApiKeyStorage {
    private val store = mutableMapOf<String, String>()

    override fun save(provider: AiProvider, apiKey: String) {
        store[provider.prefKey] = apiKey
    }

    override fun get(provider: AiProvider): String? =
        store[provider.prefKey]?.takeIf { it.isNotBlank() }

    override fun remove(provider: AiProvider) {
        store.remove(provider.prefKey)
    }

    override fun has(provider: AiProvider): Boolean = get(provider) != null

    override fun configuredProviders(): Set<AiProvider> =
        AiProvider.entries.filterTo(mutableSetOf()) { has(it) }

    override fun clearAll() {
        store.clear()
    }
}
