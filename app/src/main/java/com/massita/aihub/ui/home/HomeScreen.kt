package com.massita.aihub.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.massita.aihub.ui.home.components.FeatureCard
import com.massita.aihub.ui.home.components.HeroSection
import com.massita.aihub.ui.home.components.SectionHeading
import com.massita.aihub.ui.main.AiHubFeature
import com.massita.aihub.ui.main.MainUiState
import com.massita.aihub.ui.theme.AiHubTheme

@Composable
fun HomeScreen(
    uiState: MainUiState,
    onConnectModel: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Scaffold(modifier = modifier) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 20.dp,
                top = innerPadding.calculateTopPadding() + 20.dp,
                end = 20.dp,
                bottom = innerPadding.calculateBottomPadding() + 24.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                HeroSection(
                    title = uiState.headerTitle,
                    subtitle = uiState.subtitle,
                    primaryActionLabel = uiState.primaryActionLabel,
                    secondaryActionLabel = uiState.secondaryActionLabel,
                    onPrimaryAction = onConnectModel
                )
            }

            item {
                SectionHeading(
                    title = "Core capabilities",
                    subtitle = "A starting point for model configuration, browsing, and on-device agent execution."
                )
            }

            items(uiState.features) { feature ->
                FeatureCard(feature = feature)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    val previewState = MainUiState(
        headerTitle = "AI Hub",
        subtitle = "Configure your AI models, explore the web, and launch device tasks from one Android app.",
        primaryActionLabel = "Connect first model",
        secondaryActionLabel = "Explore task ideas",
        features = listOf(
            AiHubFeature("Model Setup", "Manage providers, defaults, and credentials.", "AI"),
            AiHubFeature("Web Navigation", "Search, browse, and summarize the web.", "WEB"),
            AiHubFeature("Device Tasks", "Run repeatable workflows on the device.", "RUN")
        )
    )

    AiHubTheme {
        Surface {
            HomeScreen(uiState = previewState)
        }
    }
}
