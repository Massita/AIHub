package com.massita.aihub.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun AiHubApp(
    modifier: Modifier = Modifier,
    content: @Composable (
        destination: TopLevelDestination,
        onNavigate: (TopLevelDestination) -> Unit,
        modifier: Modifier
    ) -> Unit
) {
    var selectedDestination by rememberSaveable { mutableStateOf(TopLevelDestination.Chat) }

    Row(modifier = modifier.fillMaxSize()) {
        AppNavigationRail(
            selectedDestination = selectedDestination,
            onDestinationSelected = { selectedDestination = it }
        )

        AnimatedContent(
            targetState = selectedDestination,
            modifier = Modifier.weight(1f),
            transitionSpec = {
                val direction = targetState.ordinal.compareTo(initialState.ordinal)
                if (direction >= 0) {
                    (slideInHorizontally { it / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { -it / 3 } + fadeOut())
                } else {
                    (slideInHorizontally { -it / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { it / 3 } + fadeOut())
                }
            },
            label = "destination_content"
        ) { destination ->
            content(
                destination,
                { selectedDestination = it },
                Modifier.fillMaxSize()
            )
        }
    }
}
