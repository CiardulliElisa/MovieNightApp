package com.mobile_systems.android.movienight.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mobile_systems.android.movienight.R
import com.mobile_systems.android.movienight.model.Movie
import com.mobile_systems.android.movienight.ui.MovieUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCarousel(
    title: String,
    movieUiState: MovieUiState,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp)
        )

        when (movieUiState) {
            is MovieUiState.Loading -> {
                HorizontalUncontainedCarousel(
                    state = rememberCarouselState { 5 },
                    itemWidth = 120.dp,
                    itemSpacing = 8.dp,
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) { _ ->
                    LoadingItemPlaceholder()
                }
            }
            is MovieUiState.Success -> {
                val movies = movieUiState.movies
                if (movies.isEmpty()) {
                    Text("No movies found for this category", modifier = Modifier.padding(16.dp))
                } else {
                    HorizontalUncontainedCarousel(
                        state = rememberCarouselState { movies.size },
                        itemWidth = 120.dp,
                        itemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) { index ->
                        val movie = movies[index]
                        MovieCard(movie = movie, onClick = { onMovieClick(movie) })
                    }
                }
            }
            is MovieUiState.Error -> {
                ErrorItemPlaceholder()
            }
        }
    }
}

@Composable
private fun MovieCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(180.dp)
            .width(120.dp),
        shape = MaterialTheme.shapes.extraLarge,
        onClick = onClick,
        tonalElevation = 2.dp
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                // Use the nested path from your new Movie model
                .data(movie.info.image)
                .crossfade(true)
                .build(),
            error = painterResource(R.drawable.littlemisssunshinejpg),
            placeholder = painterResource(R.drawable.littlemisssunshinejpg),
            // Use the nested path for the title
            contentDescription = movie.info.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun LoadingItemPlaceholder() {
    Surface(
        modifier = Modifier
            .height(180.dp)
            .width(120.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ErrorItemPlaceholder() {
    Surface(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(100.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Warning, contentDescription = null)
            Spacer(Modifier.width(8.dp))
        }
    }
}