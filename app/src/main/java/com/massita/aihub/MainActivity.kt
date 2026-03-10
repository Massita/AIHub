package com.massita.aihub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.massita.aihub.ui.main.AiHubFeature
import com.massita.aihub.ui.main.MainUiState
import com.massita.aihub.ui.main.MainViewModel
import com.massita.aihub.ui.settings.ApiKeySettingsScreen
import com.massita.aihub.ui.settings.ApiKeySettingsViewModel
import com.massita.aihub.ui.theme.AiHubTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

enum class TopLevelDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Chat(R.string.nav_chat, Icons.AutoMirrored.Outlined.Chat),
    Browser(R.string.nav_browser, Icons.Outlined.Public),
    Tasks(R.string.nav_tasks, Icons.Outlined.TaskAlt),
    Configuration(R.string.nav_configuration, Icons.Outlined.Settings)
}

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    private val settingsViewModel: ApiKeySettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by mainViewModel.uiState().collectAsStateWithLifecycle()
            var showSettings by rememberSaveable { mutableStateOf(false) }
            var selectedDestination by rememberSaveable { mutableStateOf(TopLevelDestination.Chat) }

            AiHubTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AnimatedContent(
                        targetState = showSettings,
                        transitionSpec = {
                            if (targetState) {
                                slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                            } else {
                                slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                            }
                        },
                        label = "nav"
                    ) { settings ->
                        if (settings) {
                            ApiKeySettingsScreen(
                                viewModel = settingsViewModel,
                                onBack = { showSettings = false }
                            )
                        } else {
                            Row(modifier = Modifier.fillMaxSize()) {
                                AppNavigationRail(
                                    selectedDestination = selectedDestination,
                                    onDestinationSelected = { selectedDestination = it }
                                )
                                MainRoute(
                                    uiState = uiState,
                                    onConnectModel = { showSettings = true },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppNavigationRail(
    selectedDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationRail(modifier = modifier.fillMaxHeight()) {
        TopLevelDestination.entries.forEach { destination ->
            NavigationRailItem(
                selected = selectedDestination == destination,
                onClick = { onDestinationSelected(destination) },
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = stringResource(destination.labelRes)
                    )
                },
                label = { Text(text = stringResource(destination.labelRes)) }
            )
        }
    }
}

@Composable
private fun MainRoute(
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

@Composable
private fun HeroSection(
    title: String,
    subtitle: String,
    primaryActionLabel: String,
    secondaryActionLabel: String,
    onPrimaryAction: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.22f),
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                        )
                    )
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(onClick = onPrimaryAction) {
                    Text(text = primaryActionLabel)
                }
                OutlinedButton(onClick = {}) {
                    Text(text = secondaryActionLabel)
                }
            }
        }
    }
}

@Composable
private fun SectionHeading(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun FeatureCard(feature: AiHubFeature) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = feature.badge,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = feature.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = feature.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MainRoutePreview() {
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
            MainRoute(uiState = previewState)
        }
    }
}
