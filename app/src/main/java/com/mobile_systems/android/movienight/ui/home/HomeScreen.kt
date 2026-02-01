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
import androidx.compose.foundation.lazy.LazyRow
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
    val themeUiState by themeViewModel.uiState.collectAsState()
    val movieDetailsUiState = movieDetailsViewModel.movieDetailsUiState
    val movieUiState = movieViewModel.movieUiState
    val homeUiState by homeViewModel.homeUiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val categories = listOf("Drama", "Animation", "Science Fiction", "Mystery")
    val isDetailVisible = movieDetailsUiState.id != ""

    Row(modifier = modifier.fillMaxSize()) {

        // --- LEFT SIDE: MOVIE LISTS ---
        Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                    MovieCarousel(
                        title = "Watchlist",
                        movies = homeUiState.moviesToWatch,
                        isLoading = false,
                        onMovieClick = { movie ->
                            movie.data.movieId?.let { movieDetailsViewModel.selectMovie(it) }
                        }
                    )

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
                                movie.data?.movieId?.let { movieDetailsViewModel.selectMovie(it) }
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
        }

        // --- RIGHT SIDE / DIALOG: DETAIL VIEW ---
        if (contentType == MovieNightContentType.LIST_AND_DETAIL) {
            // SIDE PANEL DESIGN (For Tablet/Landscape)
            AnimatedVisibility(
                visible = isDetailVisible,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                modifier = Modifier.width(400.dp).fillMaxHeight()
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
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
            // MODAL DIALOG DESIGN (For Phone)
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

/**
 * Shared Content Logic used by both the Side Panel and the Dialog
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailsContent(
    movieDetailsUiState: MovieDetailsUiState,
    onClose: () -> Unit,
    onToWatchClicked: () -> Unit,
    onWatchedClicked: () -> Unit,
    isExpanded: Boolean
) {
    val movie = movieDetailsUiState.selectedMovie
    val iconPainter = rememberVectorPainter(image = Icons.Default.Movie)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isExpanded) 32.dp else 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // CLOSE BUTTON
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        // IMAGE
        Surface(
            modifier = Modifier
                .width(if (isExpanded) 320.dp else 260.dp)
                .height(if (isExpanded) 180.dp else 150.dp)
                .clip(MaterialTheme.shapes.extraLarge),
            tonalElevation = 4.dp,
            shape = MaterialTheme.shapes.extraLarge
        ) {
            AsyncImage(
                model = movie.content?.thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TITLE
        Text(
            text = movie.title,
            style = if (isExpanded) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // GENRES
        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 12.dp).fillMaxWidth()
        ) {
            movie.content?.genres?.forEach { genre ->
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = genre,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(if (isExpanded) 48.dp else 24.dp))

        // ACTIONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DetailActionItem(
                label = "Save",
                icon = if (movieDetailsUiState.isToWatch) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                onClick = onToWatchClicked
            )
            DetailActionItem(
                label = "Watched",
                icon = if (movieDetailsUiState.isWatched) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                onClick = onWatchedClicked
            )
        }
    }
}

@Composable
private fun DetailActionItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        }
        Text(label, style = MaterialTheme.typography.labelMedium)
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
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                items(10) { LoadingItemPlaceholder() }
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
            .width(260.dp)
            .fillMaxWidth()
            .clip(cardShape),
        shape = cardShape,
        onClick = onClick,
        tonalElevation = 4.dp
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.thumbnail)
                .crossfade(true).build(),
            error = iconPainter,
            placeholder = iconPainter,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun LoadingItemPlaceholder() {
    Surface(
        modifier = Modifier
            .height(150.dp)
            .width(260.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 2.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.width(32.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }
    }
}