package com.massita.aihub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.massita.aihub.ui.home.HomeScreen
import com.massita.aihub.ui.main.MainViewModel
import com.massita.aihub.ui.navigation.AiHubApp
import com.massita.aihub.ui.navigation.TopLevelDestination
import com.massita.aihub.ui.settings.ApiKeySettingsScreen
import com.massita.aihub.ui.settings.ApiKeySettingsViewModel
import com.massita.aihub.ui.theme.AiHubTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModel()
    private val settingsViewModel: ApiKeySettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by mainViewModel.uiState().collectAsStateWithLifecycle()

            AiHubTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AiHubApp { destination, onNavigate, modifier ->
                        when (destination) {
                            TopLevelDestination.Chat -> HomeScreen(
                                uiState = uiState,
                                onConnectModel = { onNavigate(TopLevelDestination.Configuration) },
                                modifier = modifier
                            )
                            TopLevelDestination.Configuration -> ApiKeySettingsScreen(
                                viewModel = settingsViewModel,
                                onBack = { onNavigate(TopLevelDestination.Chat) },
                                modifier = modifier
                            )
                            TopLevelDestination.Browser,
                            TopLevelDestination.Tasks -> HomeScreen(
                                uiState = uiState,
                                onConnectModel = { onNavigate(TopLevelDestination.Configuration) },
                                modifier = modifier
                            )
                        }
                    }
                }
            }
        }
    }
}
