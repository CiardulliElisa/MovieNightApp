package com.mobile_systems.android.movienight.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun MovieNightEventNavBar(
    onTryAgainClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = onTryAgainClick,
            label = { Text("Try Again") },
            icon = { Icon(Icons.Default.Refresh, contentDescription = "Try Again") }
        )
    }
}