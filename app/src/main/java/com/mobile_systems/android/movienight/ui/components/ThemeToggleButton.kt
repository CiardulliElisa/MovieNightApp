package com.mobile_systems.android.movienight.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ThemeToggleButton(
    isDarkTheme: Boolean, // Add this to track state
    onThemeToggle: () -> Unit
) {
    IconButton(onClick = onThemeToggle) {
        Icon(
            // Switch icon based on the current theme
            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}