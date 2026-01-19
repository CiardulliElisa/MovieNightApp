package com.mobile_systems.android.movienight.ui

import android.graphics.Movie
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mobile_systems.android.movienight.ui.components.MovieCarousel
import com.mobile_systems.android.movienight.ui.components.MovieSearchBar

val categories = listOf(
    "Trending Now",
    "Watchlist",
    "Action Movies",
    "Comedy Hits",
    "Documentaries"
)

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    //Categories to be labels for the movie carousels
    val categories = listOf(
        "Trending Now",
        "Watchlist",
        "Action Movies",
        "Comedy Hits",
        "Documentaries"
    )

    //Keeps the search bar text safe during screen rotations
    val searchState = rememberTextFieldState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        //Create the Search Bar
        MovieSearchBar(
            textFieldState = searchState,
            onSearch = { query ->
                // This code runs when the user hits 'Search' on their keyboard
                println("User is looking for: $query")
            }
        )

        //Create a carousel for each category
        for (category in categories) {
            MovieCarousel(title = category)
        }
    }
}