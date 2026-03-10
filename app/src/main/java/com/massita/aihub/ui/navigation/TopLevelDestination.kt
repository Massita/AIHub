package com.massita.aihub.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector
import com.massita.aihub.R

enum class TopLevelDestination(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Chat(R.string.nav_chat, Icons.AutoMirrored.Outlined.Chat),
    Browser(R.string.nav_browser, Icons.Outlined.Public),
    Tasks(R.string.nav_tasks, Icons.Outlined.TaskAlt),
    Configuration(R.string.nav_configuration, Icons.Outlined.Settings)
}
