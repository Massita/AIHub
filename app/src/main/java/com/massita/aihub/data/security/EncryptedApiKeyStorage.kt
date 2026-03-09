package com.massita.aihub.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.massita.aihub.data.model.AiProvider

class EncryptedApiKeyStorage(context: Context) : SecureApiKeyStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override fun save(provider: AiProvider, apiKey: String) {
        prefs.edit().putString(provider.prefKey, apiKey).apply()
    }

    override fun get(provider: AiProvider): String? =
        prefs.getString(provider.prefKey, null)?.takeIf { it.isNotBlank() }

    override fun remove(provider: AiProvider) {
        prefs.edit().remove(provider.prefKey).apply()
    }

    override fun has(provider: AiProvider): Boolean = get(provider) != null

    override fun configuredProviders(): Set<AiProvider> =
        AiProvider.entries.filterTo(mutableSetOf()) { has(it) }

    override fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_FILE_NAME = "aihub_api_keys"
    }
}
