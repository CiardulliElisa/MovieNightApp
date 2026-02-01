package com.mobile_systems.android.movienight.ui.movienightevent

import Movie
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
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
import androidx.compose.runtime.LaunchedEffect
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
import com.mobile_systems.android.movienight.ui.home.MovieDetailsContent
import com.mobile_systems.android.movienight.ui.utils.MovieNightContentType
import kotlinx.coroutines.launch

@Composable
fun RankingListScreen(
    movieNightEventViewModel: MovieNightEventViewModel,
    themeViewModel: ThemeViewModel,
    onHomeClicked: () -> Unit,
    onTryAgainClicked: () -> Unit,
    movieDetailsViewModel: MovieDetailsViewModel,
    contentType: MovieNightContentType
) {
    val movieNightEventUiState by movieNightEventViewModel.uiState.collectAsState()
    val themeUiState by themeViewModel.uiState.collectAsState()
    val movieDetailsUiState = movieDetailsViewModel.movieDetailsUiState

    val coroutineScope = rememberCoroutineScope()
    val isDetailVisible = movieDetailsUiState.id != ""

    // --- RESET SIDEBAR ON ENTRY ---
    LaunchedEffect(Unit) {
        movieDetailsViewModel.deselectMovie()
    }

    val sortedMovies = remember(movieNightEventUiState.movieList) {
        movieNightEventViewModel.getSortedRankingList()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Final Rankings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        ThemeToggleButton(isDarkTheme = themeUiState.isDarkTheme, onThemeToggle = { themeViewModel.toggleDarkTheme() })
                    }

                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(sortedMovies) { movie ->
                            RankingItem(movie = movie, onMovieClick = { selectedMovie ->
                                selectedMovie.data?.movieId?.let { id ->
                                    coroutineScope.launch { movieDetailsViewModel.selectMovie(id) }
                                }
                            })
                        }
                    }
                }
                MovieNightEventNavBar(onHomeClick = onHomeClicked, onTryAgainClick = { movieNightEventViewModel.resetMovieNight(); onTryAgainClicked() })
            }

            if (contentType == MovieNightContentType.LIST_AND_DETAIL) {
                AnimatedVisibility(visible = isDetailVisible, enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()) {
                    Surface(modifier = Modifier.width(400.dp).fillMaxHeight(), color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 2.dp) {
                        MovieDetailsContent(
                            movieDetailsUiState = movieDetailsUiState,
                            onClose = { movieDetailsViewModel.deselectMovie() },
                            onToWatchClicked = { coroutineScope.launch { movieDetailsViewModel.toggleToWatch() } },
                            onWatchedClicked = { coroutineScope.launch { movieDetailsViewModel.toggleWatched() } },
                            isExpandedSidePane = true
                        )
                    }
                }
            } else if (isDetailVisible) {
                Dialog(onDismissRequest = { movieDetailsViewModel.deselectMovie() }) {
                    MovieDetailsCard(movieDetailsUiState = movieDetailsUiState, onClose = { movieDetailsViewModel.deselectMovie() }, onToWatchClicked = { coroutineScope.launch { movieDetailsViewModel.toggleToWatch() } }, onWatchedClicked = { coroutineScope.launch { movieDetailsViewModel.toggleWatched() } })
                }
            }
        }
    }
}

@Composable
fun RankingItem(movie: Movie, onMovieClick: (Movie) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth().clickable { onMovieClick(movie) }
    ) {
        Row(modifier = Modifier.padding(8.dp).height(120.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = movie.thumbnail, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.width(180.dp).fillMaxHeight().clip(RoundedCornerShape(12.dp)))
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Check, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                    Text(text = movie.likes.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(24.dp))
                    Text(text = movie.dislikes.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}