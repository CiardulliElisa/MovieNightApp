package com.mobile_systems.android.movienight.ui.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.mobile_systems.android.movienight.ui.MovieDetailsUiState

//A dialog box with some movie information (picture, title, genres, if it has been watched or is in the watchlist)
@Composable
fun MovieDetailsCard(
    movieDetailsUiState: MovieDetailsUiState,
    onClose: () -> Unit,
    onToWatchClicked: () -> Unit,
    onWatchedClicked: () -> Unit,
) {
    //The dialog window container
    Dialog(
        onDismissRequest = onClose
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            //The actual information about the movie
            MovieDetailsContent(
                movieDetailsUiState = movieDetailsUiState,
                onClose = onClose,
                onToWatchClicked = onToWatchClicked,
                onWatchedClicked = onWatchedClicked,
            )
        }
    }
}

//Contains the information about a movie: picture, title, genres, if it has been watched or is in the watchlist
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailsContent(
    movieDetailsUiState: MovieDetailsUiState,
    onClose: () -> Unit,
    onToWatchClicked: () -> Unit,
    onWatchedClicked: () -> Unit,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
) {
    //The movie for which to display the information
    val movie = movieDetailsUiState.selectedMovie

    //Check if details are available for the movie, if not display an error instead of the details
    if (movie.title != "") {
        //Handle different padding for the the different window sizes
        var columnModifier = modifier.padding(if (isExpanded) 32.dp else 24.dp)
        if (isExpanded) {
            columnModifier = columnModifier.verticalScroll(rememberScrollState())
        }

        Column(
            modifier = columnModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Button to close
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            //Movie Picture
            Surface(
                modifier = Modifier
                    .width(if (isExpanded) 340.dp else 260.dp)
                    .height(if (isExpanded) 200.dp else 150.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Movie Title
            Text(
                text = movie.title,
                style = if (isExpanded) MaterialTheme.typography.headlineLarge else MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            // Movie Genres List
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 12.dp).fillMaxWidth()
            ) {
                //A small label to contain each genre
                movie.content?.genres?.forEach { genre ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        tonalElevation = 2.dp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = genre,
                            style = if (isExpanded) MaterialTheme.typography.labelLarge else MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                //Button to save movie to watchlist
                SaveButton(
                    label = if (movieDetailsUiState.isToWatch) "Saved" else "Save",
                    icon = if (movieDetailsUiState.isToWatch) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    onClick = onToWatchClicked
                )
                //Button to mark movie as watched
                SaveButton(
                    label = if (movieDetailsUiState.isWatched) "Watched" else "Not Watched",
                    icon = if (movieDetailsUiState.isWatched) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    onClick = onWatchedClicked
                )
            }
        }
    } else {
        ErrorLoadingDetails(
            modifier
        )
    }
}

//Button to save a movie to the watchlist or mark it as watched
@Composable
private fun SaveButton(label: String, icon: ImageVector, onClick: () -> Unit) {
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

//Tells the user that no details were found for the selected movie
@Composable
private fun ErrorLoadingDetails(
    modifier : Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Error Icon
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Error Message
        Text(
            text = "No movie details found",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}