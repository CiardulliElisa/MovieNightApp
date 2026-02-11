package com.mobile_systems.android.movienight.ui.home

import Movie
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mobile_systems.android.movienight.ui.MovieDetailsViewModel
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.components.MovieDetailsCard
import com.mobile_systems.android.movienight.ui.components.MovieDetailsContent
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton
import com.mobile_systems.android.movienight.ui.utils.MovieNightContentType
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    themeViewModel: ThemeViewModel,
    homeViewModel: HomeViewModel,
    onMovieNightClicked: () -> Unit,
    modifier: Modifier = Modifier,
    movieDetailsViewModel: MovieDetailsViewModel,
    movieListsViewModel: MovieListsViewModel,
    contentType: MovieNightContentType,
) {
    //Ui states
    val themeUiState by themeViewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val movieDetailsUiState = movieDetailsViewModel.movieDetailsUiState
    val movieUiState = movieListsViewModel.movieUiState

    //Coroutine scope
    val coroutineScope = rememberCoroutineScope()

    val categories = movieListsViewModel.getCategories()
    val isDetailVisible = movieDetailsUiState.isSelected

    // To avoid that selected movies from other screens are still selected on this screen
    LaunchedEffect(Unit) {
        movieDetailsViewModel.deselectMovie()
    }

    Row(modifier = modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            when (movieUiState) {
                //On load show the whole page loading
                is MovieUiState.Loading -> {
                    LoadingScreen(modifier = Modifier.fillMaxSize())
                }
                //On error show an alert and ask to reload the page
                is MovieUiState.Error -> {
                    ErrorScreen(
                        retryAction = { movieListsViewModel.fetchAllCategories() },
                    )
                }
                is MovieUiState.Success -> {
                    // Only show the list if the state is Success
                    Column(modifier = Modifier.fillMaxSize()) {

                        //Top row, with theme toggle
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {
                            //Button to change the theme of the app
                            ThemeToggleButton(
                                onThemeToggle = { themeViewModel.toggleDarkTheme() },
                                isDarkTheme = themeUiState.isDarkTheme
                            )
                        }

                        // Lists of movies
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(bottom = 100.dp) // Ensures button doesn't cover last carousel
                        ) {
                            //Watchlist movies list, only appear if there are movies in the watchlist
                            if(homeUiState.moviesToWatch.isNotEmpty()) {
                                item {
                                    MovieCarousel(
                                        title = "Watchlist",
                                        movies = homeUiState.moviesToWatch,
                                        onMovieClick = { movie ->
                                            movie.data.movieId?.let { movieDetailsViewModel.selectMovie(it) }
                                        }
                                    )
                                }
                            }

                            //Lists of movies per genre
                            items(categories.size) { index ->
                                val categoryName = categories[index]
                                // Extract the list from our success state map
                                val movieList = movieUiState.categories[categoryName] ?: emptyList()

                                MovieCarousel(
                                    title = categoryName,
                                    movies = movieList,
                                    onMovieClick = { movie ->
                                        movie.data.movieId?.let { movieDetailsViewModel.selectMovie(it) }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            //Button to start a new movie night event
            MovieNightButton(
                onClick = onMovieNightClicked,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
            )
        }

        //Movie details view (responsive)
        if (contentType == MovieNightContentType.EXPANDED) {
            // Side panel design for non compact devices
            AnimatedVisibility(
                visible = isDetailVisible,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                modifier = Modifier.width(420.dp).fillMaxHeight()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp,
                    modifier = Modifier.fillMaxSize()
                ) {
                    MovieDetailsContent(
                        movieDetailsUiState = movieDetailsUiState,
                        onClose = { movieDetailsViewModel.deselectMovie() },
                        onToWatchClicked = { coroutineScope.launch { movieDetailsViewModel.toggleToWatch() } },
                        onWatchedClicked = { coroutineScope.launch { movieDetailsViewModel.toggleWatched() } },
                        isExpanded = true
                    )
                }
            }
        } else if (isDetailVisible) {
            // Dialog design for compact devices
            MovieDetailsCard(
                movieDetailsUiState = movieDetailsUiState,
                onClose = { movieDetailsViewModel.deselectMovie() },
                onToWatchClicked = { coroutineScope.launch { movieDetailsViewModel.toggleToWatch() } },
                onWatchedClicked = { coroutineScope.launch { movieDetailsViewModel.toggleWatched() } },
            )
        }
    }
}

//When the movies are not loaded correctly,
// show an error message and ask the user to try again, which tries fetching the movies again
@Composable
fun ErrorScreen(
    retryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {  },
        title = {
            Text(text = "Connection Error")
        },
        text = {
            Text(text = "We couldn't load the movies. Please check your internet connection and try again.")
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = { retryAction() }
            ) {
                Text("Reload")
            }
        },
        modifier = modifier
    )
}

// Carousel of movies, each movie is clickable
@Composable
fun MovieCarousel(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {

        //Label of the movie carousel
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )
        //The actual list of movies
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(movies) { movie ->
                MovieCard(movie = movie, onClick = { onMovieClick(movie) })
            }
        }
    }
}

//A single item representing a movie in the movie carousel
@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit
) {

    val iconPainter = rememberVectorPainter(image = Icons.Default.Movie)
    val cardShape = MaterialTheme.shapes.extraLarge

    Surface(
        modifier = Modifier.height(150.dp).width(260.dp).fillMaxWidth().clip(cardShape),
        shape = cardShape,
        onClick = onClick,
        tonalElevation = 4.dp
    ) {
        AsyncImage(
            model = movie.thumbnail,
            error = iconPainter,
            placeholder = iconPainter,
            contentDescription = "Movie Poster",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

//Shows a spinner when the page is loading
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(), // Fills the whole area provided (the middle of the page)
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.width(48.dp), // "Medium" size (standard is usually 32dp or 48dp)
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

//Button to start a movie night event
@Composable
fun MovieNightButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ExtendedFloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .height(64.dp)
            .width(200.dp),
        icon = {
            Icon(
                Icons.Filled.Movie,
                "Movie Night Button"
            ) },
        text = {
            Text(
                text = "Movie Night!",
                style = MaterialTheme.typography.titleLarge
            ) },
    )
}