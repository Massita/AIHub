package com.massita.aihub.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AiHubFeature(
    val title: String,
    val summary: String,
    val badge: String
)

data class MainUiState(
    val headerTitle: String,
    val subtitle: String,
    val primaryActionLabel: String,
    val secondaryActionLabel: String,
    val features: List<AiHubFeature>
)

class MainViewModel(features: List<AiHubFeature>) : ViewModel() {
    private val uiState = MutableStateFlow(
        MainUiState(
            headerTitle = "AI Hub",
            subtitle = "Configure your AI models, explore the web, and launch device tasks from one Android app.",
            primaryActionLabel = "Connect first model",
            secondaryActionLabel = "Explore task ideas",
            features = features
        )
    )

    fun uiState(): StateFlow<MainUiState> = uiState.asStateFlow()
}
