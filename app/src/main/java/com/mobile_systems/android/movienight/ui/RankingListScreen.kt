package com.mobile_systems.android.movienight.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mobile_systems.android.movienight.data.Movie
import com.mobile_systems.android.movienight.ui.components.MovieDetailsCard
import com.mobile_systems.android.movienight.ui.components.MovieNightEventNavBar
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton

@Composable
fun RankingListScreen(
    movieNightEventViewModel: MovieNightEventViewModel,
    themeViewModel: ThemeViewModel,
    onHomeClicked: () -> Unit,
    onTryAgainClicked: () -> Unit
) {
    val movieNightEventUiState by movieNightEventViewModel.uiState.collectAsState()
    val themeUiState by themeViewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            MovieNightEventNavBar(
                onHomeClick = { onHomeClicked() },
                onTryAgainClick = { onTryAgainClicked() }
            )
        }
    ) { innerPadding ->
        // Main container
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // 1. THE LIST CONTENT
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Final Rankings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    ThemeToggleButton(isDarkTheme = themeUiState.isDarkTheme, onThemeToggle = { themeViewModel.toggleDarkTheme() })
                }

                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(movieNightEventUiState.movieList) { movie ->
                        RankingItem(movie = movie, onMovieClick = { movieNightEventViewModel.showMovieDetails() })
                    }
                }
            }
            if (movieNightEventUiState.showMovieDetails) {
                Dialog(
                    onDismissRequest = { movieNightEventViewModel.closeMovieDetails() }
                ) {
                    MovieDetailsCard(
                        onClose = { movieNightEventViewModel.closeMovieDetails() },
                    )
                }
            }
        }
    }
}

/**
 * Individual Card representing a movie and its vote count.
 */
@Composable
fun RankingItem(
    movie: Movie,
    onMovieClick: (Movie) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie) }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(120.dp), // Fixed height for a uniform list
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. POSTER PLACEHOLDER
            // Uses a secondary container color that adapts to Light/Dark theme
            Surface(
                modifier = Modifier
                    .width(80.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "FILM",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. MOVIE DATA
            // Box allows us to align title and scores independently
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.TopStart)
                )

                // VOTE COUNTER ROW
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    // Likes (Always Green for positive action)
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Likes",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = movie.likes.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp, end = 16.dp)
                    )

                    // Dislikes (Uses Theme error color, usually Red)
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dislikes",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = movie.dislikes.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}