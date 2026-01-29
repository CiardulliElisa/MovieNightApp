package com.mobile_systems.android.movienight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mobile_systems.android.movienight.ui.MovieDetailsUiState

@Composable
fun MovieDetailsCard(
    movieDetailsUiState: MovieDetailsUiState,
    onClose: () -> Unit,
    onToWatchClicked: () -> Unit,
    onWatchedClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        Column {
            // Header Image
            Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
                AsyncImage(
                    model = movieDetailsUiState.selectedMovie.trailer?.thumbnail,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // Title
                movieDetailsUiState.selectedMovie.title.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    movieDetailsUiState.selectedMovie.trailer?.genres?.forEach { genre ->
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            tonalElevation = 2.dp
                        ) {
                            Text(
                                text = genre,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Save Action
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = onToWatchClicked) {
                            Icon(
                                imageVector = if (movieDetailsUiState.isToWatch)
                                    Icons.Default.Bookmark
                                else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text("Save", style = MaterialTheme.typography.labelMedium)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = onWatchedClicked) {
                            Icon(
                                imageVector = if (movieDetailsUiState.isWatched)
                                    Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                contentDescription = "Watched",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text("Watched", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
    }
}