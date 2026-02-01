package com.mobile_systems.android.movienight.ui.home

import Movie
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mobile_systems.android.movienight.ui.MovieDetailsUiState
import com.mobile_systems.android.movienight.ui.MovieDetailsViewModel
import com.mobile_systems.android.movienight.ui.MovieUiState
import com.mobile_systems.android.movienight.ui.MovieViewModel
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.components.MovieDetailsCard
import com.mobile_systems.android.movienight.ui.components.MovieDetailsContent
import com.mobile_systems.android.movienight.ui.components.MovieNightButton
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
    movieViewModel: MovieViewModel,
    contentType: MovieNightContentType
) {
    //Ui states
    val themeUiState by themeViewModel.uiState.collectAsState()
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val movieDetailsUiState = movieDetailsViewModel.movieDetailsUiState
    val movieUiState = movieViewModel.movieUiState

    //Coroutine scope
    val coroutineScope = rememberCoroutineScope()

    val categories = movieViewModel.getCategories()
    val isDetailVisible = movieDetailsUiState.isSelected

    // To avoid that selected movies from other screens are still selected on this screen
    LaunchedEffect(Unit) {
        movieDetailsViewModel.deselectMovie()
    }

    Row(modifier = modifier.fillMaxSize()) {

        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    //Watchlist movies list
                    item {
                        MovieCarousel(
                            title = "Watchlist",
                            movies = homeUiState.moviesToWatch,
                            isLoading = false,
                            onMovieClick = { movie ->
                                movie.data.movieId?.let { movieDetailsViewModel.selectMovie(it) }
                            }
                        )
                    }

                    //Lists of movies per genre
                    items(categories.size) { index ->
                        val categoryName = categories[index]
                        val (movieList, isCategoryLoading) = when (val state = movieUiState) {
                            is MovieUiState.Success -> {
                                val list = state.categories[categoryName] ?: emptyList()
                                list to false
                            }
                            is MovieUiState.Loading -> emptyList<Movie>() to true
                            is MovieUiState.Error -> emptyList<Movie>() to false
                        }
                        MovieCarousel(
                            title = categoryName,
                            movies = movieList,
                            isLoading = isCategoryLoading,
                            onMovieClick = { movie ->
                                movie.data.movieId?.let { movieDetailsViewModel.selectMovie(it) }
                            }
                        )
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
        if (contentType == MovieNightContentType.LIST_AND_DETAIL) {
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
                        isExpandedSidePane = true
                    )
                }
            }
        } else if (isDetailVisible) {
            // Dialog design for compact devices
            Dialog(onDismissRequest = { movieDetailsViewModel.deselectMovie() }) {
                MovieDetailsCard(
                    movieDetailsUiState = movieDetailsUiState,
                    onClose = { movieDetailsViewModel.deselectMovie() },
                    onToWatchClicked = { coroutineScope.launch { movieDetailsViewModel.toggleToWatch() } },
                    onWatchedClicked = { coroutineScope.launch { movieDetailsViewModel.toggleWatched() } },
                )
            }
        }
    }
}

// Carousel of movies, each movie is clickable
@Composable
fun MovieCarousel(
    title: String,
    movies: List<Movie>,
    isLoading: Boolean,
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
            if (isLoading) {
                items(10) { LoadingItemPlaceholder() }
            } else {
                items(movies) { movie ->
                    MovieCard(movie = movie, onClick = { onMovieClick(movie) })
                }
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

//This is an empty movie card, used as placeholder while the movie loads. It only contains a spinner.
@Composable
fun LoadingItemPlaceholder() {
    val cardShape = MaterialTheme.shapes.extraLarge
    Surface(
        modifier = Modifier.height(150.dp).width(260.dp).fillMaxWidth().clip(cardShape),
        shape = cardShape,
        tonalElevation = 4.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.width(32.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
        }
    }
}