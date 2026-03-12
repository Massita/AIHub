package com.massita.aihub.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity

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
    var isBarVisible by rememberSaveable { mutableStateOf(true) }

    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    val isKeyboardOpen by remember {
        derivedStateOf { imeInsets.getBottom(density) > 0 }
    }

    LaunchedEffect(isKeyboardOpen) {
        isBarVisible = !isKeyboardOpen
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -10f) {
                    isBarVisible = false
                } else if (available.y > 10f) {
                    isBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
        bottomBar = {
            AppNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { selectedDestination = it },
                visible = isBarVisible
            )
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = selectedDestination,
            modifier = Modifier.fillMaxSize().padding(innerPadding),
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
