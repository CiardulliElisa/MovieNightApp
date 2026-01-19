package com.mobile_systems.android.movienight.ui

import android.graphics.Movie
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mobile_systems.android.movienight.ui.components.MovieCarousel

val categories = listOf(
    "Trending Now",
    "Watchlist",
    "Action Movies",
    "Comedy Hits",
    "Documentaries"
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    // 1. Create a list of the titles you want to display
    val categories = listOf(
        "Trending Now",
        "Watchlist",
        "Action Movies",
        "Comedy Hits",
        "Documentaries"
    )

    // 2. Make the Column scrollable
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 3. Loop through the list to create 5 carousels
        for (category in categories) {
            MovieCarousel(title = category)
        }
    }
}