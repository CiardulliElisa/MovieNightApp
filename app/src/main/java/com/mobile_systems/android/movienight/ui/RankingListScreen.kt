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
import com.mobile_systems.android.movienight.data.Movie
import com.mobile_systems.android.movienight.ui.components.MovieDetailsCard
import com.mobile_systems.android.movienight.ui.components.MovieNightEventNavBar

@Composable
fun RankingListScreen(
    movieNightEventViewModel: MovieNightEventViewModel,
    modifier: Modifier = Modifier,
    onHomeClicked: () -> Unit,
    onTryAgainClicked: () -> Unit
) {
    val movieNightEventUiState by movieNightEventViewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            MovieNightEventNavBar(
                onHomeClick = {
                    movieNightEventViewModel.resetMovieNight()
                    onHomeClicked()
                },
                onTryAgainClick = {
                    movieNightEventViewModel.resetMovieNight()
                    onTryAgainClicked()
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            if (movieNightEventUiState.selectedMovie != null && movieNightEventUiState.showMovieDetails) {
                MovieDetailsCard(
                    onClose = { movieNightEventViewModel.closeMovieDetails() },
                )
            } else {
                val shuffledMovies = remember(movieNightEventUiState.movieList) {
                    movieNightEventUiState.movieList.shuffled()
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Final Rankings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        // The LazyColumn handles its own bottom spacing nicely
                        contentPadding = PaddingValues(bottom = 16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(shuffledMovies) { movie ->
                            RankingItem(
                                movie = movie,
                                onMovieClick = { movieNightEventViewModel.showMovieDetails() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RankingItem(
    movie: Movie,
    onMovieClick: (Movie) -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie) }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .height(150.dp), // Match the poster height
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. POSTER
            Surface(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "POSTER",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. INFO BOX
            // We use a Box with fillMaxSize to allow internal alignment
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Movie Title at the TOP START (Top Left of this box)
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart)
                )

                // Icons at the BOTTOM END (Bottom Right of this box)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.BottomEnd)
                ) {
                    // Likes
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Yes votes",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = movie.likes.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 20.dp)
                    )

                    // Dislikes
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "No votes",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = movie.dislikes.toString(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(end = 20.dp)
                    )
                }
            }
        }
    }
}