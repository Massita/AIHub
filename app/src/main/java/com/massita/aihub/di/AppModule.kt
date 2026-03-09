package com.massita.aihub.di

import com.massita.aihub.ui.main.AiHubFeature
import com.massita.aihub.ui.main.MainViewModel
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
    viewModelOf(::MainViewModel)
}
