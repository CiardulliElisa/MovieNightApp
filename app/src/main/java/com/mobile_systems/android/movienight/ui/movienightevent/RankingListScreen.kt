package com.mobile_systems.android.movienight.ui.movienightevent

import Movie
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mobile_systems.android.movienight.ui.MovieDetailsViewModel
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.components.MovieDetailsCard
import com.mobile_systems.android.movienight.ui.components.MovieNightEventNavBar
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton
import kotlinx.coroutines.launch

@Composable
fun RankingListScreen(
    movieNightEventViewModel: MovieNightEventViewModel,
    themeViewModel: ThemeViewModel,
    onHomeClicked: () -> Unit,
    onTryAgainClicked: () -> Unit,
    movieDetailsViewModel: MovieDetailsViewModel
) {
    val movieNightEventUiState by movieNightEventViewModel.uiState.collectAsState()
    val themeUiState by themeViewModel.uiState.collectAsState()
    val movieDetailsUiState = movieDetailsViewModel.movieDetailsUiState

    val coroutineScope = rememberCoroutineScope()

    val sortedMovies = remember(movieNightEventUiState.movieList) {
        movieNightEventViewModel.getSortedRankingList()
    }

    Scaffold(
        bottomBar = {
            MovieNightEventNavBar(
                onHomeClick = { onHomeClicked() },
                onTryAgainClick = {
                    movieNightEventViewModel.resetMovieNight()
                    onTryAgainClicked()
                }
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
                    items(sortedMovies) { movie ->
                        RankingItem(
                            movie = movie,
                            onMovieClick = { selectedMovie ->
                                // FIXED: Use kinoMovie?.movieId to match your updated data model
                                val id = selectedMovie.data?.movieId
                                if (id != null) {
                                    coroutineScope.launch {
                                        movieDetailsViewModel.selectMovie(id)
                                    }
                                }
                            }
                        )
                    }
                }
            }
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
                        }
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
    onMovieClick: (Movie) -> Unit,
) {
    val iconPainter = rememberVectorPainter(Icons.Default.Movie)

    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp), // Slightly more rounded for a modern look
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie) },
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp) // Smaller padding to let the image breathe
                .height(120.dp), // Increased height to make the image "important"
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. PROMINENT THUMBNAIL
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.thumbnail)
                    .crossfade(true)
                    .build(),
                contentDescription = null, // Title removed from text, keep description null or use title
                placeholder = iconPainter,
                error = iconPainter,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(180.dp) // Much wider image
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
            )

            // 2. VOTE DATA (Centered in the remaining space)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Check,
                        "Likes",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp) // Larger icons
                    )
                    Text(
                        text = movie.likes.toString(),
                        style = MaterialTheme.typography.titleLarge, // Larger font
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Close,
                        "Dislikes",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = movie.dislikes.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}