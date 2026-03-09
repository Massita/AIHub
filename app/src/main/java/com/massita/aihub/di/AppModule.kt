package com.massita.aihub.di

import com.massita.aihub.data.repository.ApiKeyRepository
import com.massita.aihub.data.repository.ApiKeyRepositoryImpl
import com.massita.aihub.data.security.EncryptedApiKeyStorage
import com.massita.aihub.data.security.SecureApiKeyStorage
import com.massita.aihub.ui.main.AiHubFeature
import com.massita.aihub.ui.main.MainViewModel
import com.massita.aihub.ui.settings.ApiKeySettingsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

private fun buildFeatureCatalog(): List<AiHubFeature> = listOf(
    AiHubFeature(
        title = "Model Setup",
        summary = "Connect local or cloud AI providers, store credentials securely, and pick defaults per task.",
        badge = "AI"
    ),
    AiHubFeature(
        title = "Web Navigation",
        summary = "Open websites, search the web, extract answers, and keep browsing context available to agents.",
        badge = "WEB"
    ),
    AiHubFeature(
        title = "Device Tasks",
        summary = "Run multi-step automations on the device for research, productivity, and operational workflows.",
        badge = "RUN"
    ),
    AiHubFeature(
        title = "Workspace Memory",
        summary = "Track recent actions, surfaced insights, and reusable task templates across sessions.",
        badge = "MEM"
    )
)

val appModule = module {
    single { buildFeatureCatalog() }
    single<SecureApiKeyStorage> { EncryptedApiKeyStorage(get()) }
    single<ApiKeyRepository> { ApiKeyRepositoryImpl(get()) }
    viewModelOf(::MainViewModel)
    viewModelOf(::ApiKeySettingsViewModel)
}
