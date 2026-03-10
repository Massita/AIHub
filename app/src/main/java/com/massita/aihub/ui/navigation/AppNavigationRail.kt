package com.massita.aihub.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.massita.aihub.R

@Composable
fun AppNavigationRail(
    selectedDestination: TopLevelDestination,
    onDestinationSelected: (TopLevelDestination) -> Unit,
    isExpanded: Boolean,
    onToggleExpanded: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isExpanded,
        modifier = modifier.fillMaxHeight(),
        transitionSpec = {
            (expandHorizontally(expandFrom = Alignment.Start) + fadeIn())
                .togetherWith(shrinkHorizontally(shrinkTowards = Alignment.Start) + fadeOut())
        },
        label = "nav_rail_toggle"
    ) { expanded ->
        if (expanded) {
            NavigationRail(
                modifier = Modifier.fillMaxHeight(),
                header = {
                    IconButton(onClick = onToggleExpanded) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuOpen,
                            contentDescription = stringResource(R.string.collapse_navigation)
                        )
                    }
                }
            ) {
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
        } else {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconButton(onClick = onToggleExpanded) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = stringResource(R.string.expand_navigation)
                    )
                }
            }
        }
    }
}
