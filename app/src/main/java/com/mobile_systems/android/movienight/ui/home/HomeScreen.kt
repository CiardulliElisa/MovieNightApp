package com.mobile_systems.android.movienight.ui.home

import Movie
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mobile_systems.android.movienight.ui.MovieDetailsViewModel
import com.mobile_systems.android.movienight.ui.MovieUiState
import com.mobile_systems.android.movienight.ui.MovieViewModel
import com.mobile_systems.android.movienight.ui.ThemeViewModel
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
    movieViewModel: MovieViewModel
) {
    val themeUiState by themeViewModel.uiState.collectAsState()
    val movieDetailsUiState = movieDetailsViewModel.movieUiState
    val movieUiState = movieViewModel.movieUiState

    val coroutineScope = rememberCoroutineScope()
    val searchState = rememberTextFieldState()

    val categories = listOf("Drama", "Animation", "Science Fiction")

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MovieSearchBar(
                    textFieldState = searchState,
                    onSearch = { query -> /* your search logic */ },
                    modifier = Modifier.weight(1f)
                )

                ThemeToggleButton(
                    onThemeToggle = { themeViewModel.toggleDarkTheme() },
                    isDarkTheme = themeUiState.isDarkTheme
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                for (category in categories) {
                    val (movieList, isCategoryLoading) = when (movieUiState) {
                        is MovieUiState.Success -> {
                            val list = movieUiState.categories[category] ?: emptyList()
                            list to (movieUiState.categories[category] == null)
                        }
                        is MovieUiState.Loading -> emptyList<Movie>() to true
                        is MovieUiState.Error -> emptyList<Movie>() to false
                    }

                    MovieCarousel(
                        title = category,
                        movies = movieList,
                        isLoading = isCategoryLoading,
                        onMovieClick = { movie ->
                            val id = movie.data?.movieId
                            if (id != null) {
                                movieDetailsViewModel.selectMovie(id)
                            }
                        }
                    )
                }
            }
        }

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

@Composable
fun MovieCarousel(
    title: String,
    movies: List<Movie>,
    isLoading: Boolean,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp)
        )

        // Using LazyRow instead of Carousel to prevent shape morphing
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                items(10) {
                    LoadingItemPlaceholder()
                }
            } else {
                itemsIndexed(movies) { _, movie ->
                    MovieCard(movie = movie, onClick = { onMovieClick(movie) })
                }
            }
        }
    }
}

@Composable
private fun MovieCard(movie: Movie, onClick: () -> Unit) {
    val iconPainter = rememberVectorPainter(image = Icons.Default.Movie)
    val cardShape = MaterialTheme.shapes.extraLarge

    Surface(
        modifier = Modifier
            .height(150.dp)
            .fillMaxWidth()
            .clip(cardShape),
        shape = cardShape,
        onClick = onClick,
        tonalElevation = 4.dp
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.thumbnail)
                .crossfade(true)
                .build(),
            error = iconPainter,
            placeholder = iconPainter,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
    // Spacer and Text title have been removed from here
}

@Composable
fun LoadingItemPlaceholder() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        modifier = Modifier
            .height(150.dp)
            .width(260.dp)
            .graphicsLayer(alpha = alpha),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Box(modifier = Modifier.fillMaxSize())
    }
}