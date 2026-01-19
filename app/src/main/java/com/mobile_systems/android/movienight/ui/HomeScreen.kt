package com.mobile_systems.android.movienight.ui

import android.graphics.Movie
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobile_systems.android.movienight.ui.components.MovieCarousel
import com.mobile_systems.android.movienight.ui.components.MovieNightButton
import com.mobile_systems.android.movienight.ui.components.MovieSearchBar

val categories = listOf("Trending Now", "Watchlist", "Action Movies", "Comedy Hits", "Documentaries")

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val searchState = rememberTextFieldState()

    // We use a Box to layer the button over the scrollable content
    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {
            // 1. The Top Row (Sticky)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MovieSearchBar(
                    textFieldState = searchState,
                    onSearch = { query -> println(query) },
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { /* Open Settings */ }) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = "Dark Mode",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 2. The Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                for (category in categories) {
                    MovieCarousel(title = category)
                }
            }
        }

        // 3. The Floating Button (Bottom Right)
        MovieNightButton(
            onClick = { /* Action */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp) // Standard FAB margin from screen edges
        )
    }
}