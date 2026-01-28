package com.mobile_systems.android.movienight.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mobile_systems.android.movienight.ui.MovieDetailsViewModel
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.components.MovieCarousel
import com.mobile_systems.android.movienight.ui.components.MovieDetailsCard
import com.mobile_systems.android.movienight.ui.components.MovieNightButton
import com.mobile_systems.android.movienight.ui.components.MovieSearchBar
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    themeViewModel: ThemeViewModel,
    homeViewModel: HomeViewModel,
    onMovieNightClicked: () -> Unit,
    modifier: Modifier = Modifier,
    movieDetailsViewModel: MovieDetailsViewModel,
) {
    val homeUiState by homeViewModel.uiState.collectAsState()
    val themeUiState by themeViewModel.uiState.collectAsState()
    val movieDetailsUiState = movieDetailsViewModel.movieUiState

    val coroutineScope = rememberCoroutineScope()
    val searchState = rememberTextFieldState()

    val categories = listOf("Trending Now", "Watchlist", "Action Movies", "Comedy Hits")

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

                ThemeToggleButton(
                    onThemeToggle = { themeViewModel.toggleDarkTheme() },
                    isDarkTheme = themeUiState.isDarkTheme
                )
            }

            //Movie lists divided by movie category
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                for (category in categories) {
                    MovieCarousel(
                        title = category,
                        onMovieClick = { carouselItem ->
                            coroutineScope.launch {
                                movieDetailsViewModel.selectMovie(carouselItem.id)
                            }
                        }
                    )
                }
            }
        }

        // Button to start a movie night event
        MovieNightButton(
            onClick = onMovieNightClicked,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )

        if (movieDetailsUiState.id != "") {
            Dialog(
                onDismissRequest = { movieDetailsViewModel.deselectMovie() }
            ) {
                MovieDetailsCard(
                    movieDetailsUiState = movieDetailsUiState,
                    onClose = { movieDetailsViewModel.deselectMovie() },
                    onToWatchClicked = {
                        coroutineScope.launch { movieDetailsViewModel.toggleToWatch() }
                    },
                    onWatchedClicked = {
                        coroutineScope.launch { movieDetailsViewModel.toggleWatched() }
                    },
                )
            }
        }
    }
}