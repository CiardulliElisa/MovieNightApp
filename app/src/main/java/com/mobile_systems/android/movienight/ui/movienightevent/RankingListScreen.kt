package com.mobile_systems.android.movienight.ui.movienightevent

import Movie
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.mobile_systems.android.movienight.ui.MovieDetailsViewModel
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.components.MovieDetailsCard
import com.mobile_systems.android.movienight.ui.components.MovieDetailsContent
import com.mobile_systems.android.movienight.ui.components.MovieNightEventNavBar
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton
import com.mobile_systems.android.movienight.ui.utils.MovieNightContentType
import kotlinx.coroutines.launch

/**This screen shows a list of movies, ranked by their likes and dislikes, that were gathered throughout the movie night event*/
@Composable
fun RankingListScreen(
    movieNightEventViewModel: MovieNightEventViewModel,
    themeViewModel: ThemeViewModel,
    onHomeClicked: () -> Unit,
    onTryAgainClicked: () -> Unit,
    movieDetailsViewModel: MovieDetailsViewModel,
    contentType: MovieNightContentType
) {
    //Ui States
    val movieNightEventUiState by movieNightEventViewModel.uiState.collectAsState()
    val themeUiState by themeViewModel.uiState.collectAsState()
    val movieDetailsUiState = movieDetailsViewModel.movieDetailsUiState

    // Coroutine scope
    val coroutineScope = rememberCoroutineScope()

    val isDetailVisible = movieDetailsUiState.isSelected

    //Deselect the movie that may have been selected in another screen
    LaunchedEffect(Unit) {
        movieDetailsViewModel.deselectMovie()
    }

    //Remember the sorted list of movies, to avoid recalculating each time
    val sortedMovies = remember(movieNightEventUiState.movieList) {
        movieNightEventViewModel.getSortedRankingList()
    }

    Scaffold(modifier = Modifier.fillMaxSize()) {
        innerPadding ->
        Row(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    //Top row with title and theme toggle
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        //Title of page: Final Ranking
                        Text(
                            "Final Rankings",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold)
                        //Button to change the theme
                        ThemeToggleButton(
                            isDarkTheme = themeUiState.isDarkTheme,
                            onThemeToggle = { themeViewModel.toggleDarkTheme() })
                    }
                    //List of ranked movies
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(sortedMovies) { movie ->
                            RankingItem(
                                movie = movie,
                                onMovieClick = { selectedMovie ->
                                selectedMovie.data.movieId?.let { id ->
                                    coroutineScope.launch { movieDetailsViewModel.selectMovie(id) }
                                }
                            })
                        }
                    }
                }
                //navbar to go back to home or try again by starting from the adding friends page
                MovieNightEventNavBar(
                    onTryAgainClick = { movieNightEventViewModel.resetMovieNight(); onTryAgainClicked() },
                    onHomeClick = onHomeClicked,
                )
            }

            //Responsive movie details page design
            if (contentType == MovieNightContentType.LIST_AND_DETAIL) {
                //If the screen is not compact, show a side panel with the movie details
                AnimatedVisibility(visible = isDetailVisible, enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn()) {
                    Surface(modifier = Modifier.width(400.dp).fillMaxHeight(), color = MaterialTheme.colorScheme.surfaceContainerLow, tonalElevation = 2.dp) {
                        MovieDetailsContent(
                            movieDetailsUiState = movieDetailsUiState,
                            onClose = { movieDetailsViewModel.deselectMovie() },
                            onToWatchClicked = { coroutineScope.launch { movieDetailsViewModel.toggleToWatch() } },
                            onWatchedClicked = { coroutineScope.launch { movieDetailsViewModel.toggleWatched() } },
                            isExpanded = true
                        )
                    }
                }
                //If the screen is compact show a dialog box with the movie details
            } else if (isDetailVisible) {
                Dialog(onDismissRequest = { movieDetailsViewModel.deselectMovie() }) {
                    MovieDetailsCard(movieDetailsUiState = movieDetailsUiState, onClose = { movieDetailsViewModel.deselectMovie() }, onToWatchClicked = { coroutineScope.launch { movieDetailsViewModel.toggleToWatch() } }, onWatchedClicked = { coroutineScope.launch { movieDetailsViewModel.toggleWatched() } })
                }
            }
        }
    }
}

//A single item, representing a movie and the votes it got, used to display the ranking
@Composable
fun RankingItem(
    movie: Movie,
    onMovieClick: (Movie) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie) }
    ) {
        Row(
            modifier = Modifier.padding(8.dp).height(120.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.thumbnail,
                contentDescription = "Movie image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(180.dp).fillMaxHeight().clip(MaterialTheme.shapes.extraLarge)
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //Row for likes information
                Row(
                    verticalAlignment = Alignment.CenterVertically)
                {
                    //Likes icon
                    Icon(
                        Icons.Default.Check,
                        null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp))
                    //Number of positive votes
                    Text(
                        text = movie.likes.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))

                //Row for dislike information
                Row(verticalAlignment = Alignment.CenterVertically) {
                    //Dislike Icon
                    Icon(
                        Icons.Default.Close,
                        null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp))
                    //Number of negative votes
                    Text(
                        text = movie.dislikes.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
    }
}