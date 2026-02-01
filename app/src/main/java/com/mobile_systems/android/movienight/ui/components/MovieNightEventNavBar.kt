package com.mobile_systems.android.movienight.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

//A navbar for the movie night event screens, to either go back to the home page or try again and start a new movie night event from scratch
@Composable
fun MovieNightEventNavBar(
    onTryAgainClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        //Home Button
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
        )
        //Try Again Button
        NavigationBarItem(
            selected = false,
            onClick = onTryAgainClick,
            label = { Text("Try Again") },
            icon = { Icon(Icons.Default.Refresh, contentDescription = "Try Again") }
        )
    }
}