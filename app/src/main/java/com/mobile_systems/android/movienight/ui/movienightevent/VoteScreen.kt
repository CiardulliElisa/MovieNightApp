package com.mobile_systems.android.movienight.ui.movienightevent

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.mobile_systems.android.movienight.ui.MovieDetailsViewModel
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.components.MovieDetailsCard
import com.mobile_systems.android.movienight.ui.components.MovieDetailsContent
import com.mobile_systems.android.movienight.ui.components.MovieNightEventNavBar
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton
import com.mobile_systems.android.movienight.ui.utils.MovieNightContentType
import kotlinx.coroutines.launch

/**This screen is used in turns by the various participants of a movie night event to vote on the movies they like and dislike*/
@Composable
fun VoteScreen(
    movieNightEventViewModel: MovieNightEventViewModel,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
    onMovieNightFinished: () -> Unit,
    onHomeClicked: () -> Unit,
    onTryAgainClicked: () -> Unit,
    movieDetailsViewModel: MovieDetailsViewModel,
    contentType: MovieNightContentType
) {

    // Ui states
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

    //Handle the end of the movie night event
    LaunchedEffect(movieNightEventUiState.isMovieNightFinished) {
        if (movieNightEventUiState.isMovieNightFinished) {
            onMovieNightFinished()
        }
    }

    //
    Scaffold(modifier = modifier.fillMaxSize()) {
        innerPadding ->

        //Contains the voting process and the side panel (on non compact devices)
        Row(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
            //Left column - main page
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight()
            ) {
                // Theme Toggle Button
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    ThemeToggleButton(isDarkTheme = themeUiState.isDarkTheme, onThemeToggle = { themeViewModel.toggleDarkTheme() })
                }

                // Header: participant's name and icon, as chosen in the screen before (add friends)
                movieNightEventUiState.currentFriend?.let { friend ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        //Participant's Icon
                        Surface(
                            shape = CircleShape,
                            color = friend.color,
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                friend.icon,
                                "Randomly selected icon",
                                tint = Color.White,
                                modifier = Modifier.padding(10.dp))
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        //Participant's name
                        Text(
                            friend.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Movie Picture - thumbnail
                Card(
                    shape = MaterialTheme.shapes.extraLarge,
                    modifier =
                        Modifier.fillMaxWidth()
                            .height(220.dp)
                            .padding(horizontal = 16.dp)
                        .clickable { movieNightEventUiState.currentMovie?.data?.movieId?.let { id -> movieDetailsViewModel.selectMovie(id) } },
                    elevation = CardDefaults.cardElevation(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(movieNightEventUiState.currentMovie?.thumbnail).crossfade(true).build(),
                            contentDescription = "",
                            placeholder = rememberVectorPainter(Icons.Default.Movie),
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        //Small info icon on the clickable movie picture
                        Icon(Icons.Default.Info, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(24.dp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Voting row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    //Dislike button
                    VoteButton(Icons.Default.Close, Color.Red, { movieNightEventViewModel.updateDislikes() }, "Dislike")

                    //Likes Button
                    VoteButton(Icons.Default.Check, Color(0xFF4CAF50), { movieNightEventViewModel.updateLikes() }, "Like")
                }
                Spacer(modifier = Modifier.weight(1.2f))
            }

            if (movieNightEventUiState.showNewFriendDialog) {
                TurnConfirmationDialog(
                    friendName = movieNightEventUiState.currentFriend?.name,
                    friendIcon = movieNightEventUiState.currentFriend?.icon,
                    friendColor = movieNightEventUiState.currentFriend?.color,
                    onConfirm = { movieNightEventViewModel.closeNewFriendDialog() }
                )
            }
        }
        MovieNightEventNavBar(
            onHomeClick = { movieNightEventViewModel.resetMovieNight(); onHomeClicked() },
            onTryAgainClick = { movieNightEventViewModel.resetMovieNight(); onTryAgainClicked() }
        )
    }

    // RIGHT COLUMN (DETAIL)
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

@Composable
fun VoteButton(icon: ImageVector, tint: Color, onClick: () -> Unit, contentDescription: String) {
    IconButton(onClick = onClick, modifier = Modifier.size(80.dp)) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp, tonalElevation = 2.dp) {
            Icon(icon, contentDescription, tint = tint, modifier = Modifier.size(54.dp).padding(12.dp))
        }
    }
}

@Composable
fun TurnConfirmationDialog(friendName: String?, friendIcon: ImageVector?, friendColor: Color?, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        icon = { friendIcon?.let { Icon(it, null, tint = friendColor ?: Color.Gray, modifier = Modifier.size(64.dp)) } },
        title = { Text("It's ${friendName ?: "Someone"}'s Turn!", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        text = { Text("Pass the phone to $friendName. Ready to vote?", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        confirmButton = { Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth()) { Text("I'm Ready!") } }
    )
}