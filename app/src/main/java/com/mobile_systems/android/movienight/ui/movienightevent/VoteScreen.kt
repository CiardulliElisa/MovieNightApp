package com.mobile_systems.android.movienight.ui.movienightevent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mobile_systems.android.movienight.ui.MovieDetailsViewModel
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.components.MovieDetailsCard
import com.mobile_systems.android.movienight.ui.components.MovieNightEventNavBar
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton
import kotlinx.coroutines.launch

@Composable
fun VoteScreen(
    movieNightEventViewModel: MovieNightEventViewModel,
    themeViewModel: ThemeViewModel,
    modifier: Modifier = Modifier,
    onMovieNightFinished: () -> Unit,
    onHomeClicked: () -> Unit,
    onTryAgainClicked: () -> Unit,
    movieDetailsViewModel: MovieDetailsViewModel
) {
    val movieNightEventUiState by movieNightEventViewModel.uiState.collectAsState()
    val themeUiState by themeViewModel.uiState.collectAsState()
    val movieDetailsUiState = movieDetailsViewModel.movieUiState

    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(movieNightEventUiState.isMovieNightFinished) {
        if (movieNightEventUiState.isMovieNightFinished) {
            onMovieNightFinished()
        }
    }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            MovieNightEventNavBar(
                onHomeClick = {
                    movieNightEventViewModel.resetMovieNight()
                    onHomeClicked()
                },
                onTryAgainClick = {
                    movieNightEventViewModel.resetMovieNight()
                    onTryAgainClicked()
                }
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // --- LAYER 1: THEME TOGGLE (Top Right Overlay) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                ThemeToggleButton(
                    isDarkTheme = themeUiState.isDarkTheme,
                    onThemeToggle = { themeViewModel.toggleDarkTheme() }
                )
            }

            // --- LAYER 2: MAIN CONTENT ---
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // CENTERED VOTER TITLE (Icon + Name Row)
                movieNightEventUiState.currentFriend?.let { friend ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = friend.color.copy(alpha = 0.2f),
                            modifier = Modifier.size(48.dp) // Smaller, balanced icon
                        ) {
                            Icon(
                                imageVector = friend.icon,
                                contentDescription = null,
                                tint = friend.color,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = friend.name,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // MOVIE POSTER CARD
                Card(
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .size(300.dp, 450.dp)
                        .clickable {
                            movieNightEventUiState.currentMovie?.id?.let { id ->
                                movieDetailsViewModel.selectMovie(id)
                            }
                        },
                    elevation = CardDefaults.cardElevation(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = movieNightEventUiState.currentMovie?.title ?: "Selecting Movie...",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(32.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // VOTING BUTTONS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    VoteButton(
                        icon = Icons.Default.Close,
                        tint = Color.Red,
                        onClick = { movieNightEventViewModel.updateDislikes() },
                        contentDescription = "Dislike"
                    )

                    VoteButton(
                        icon = Icons.Default.Check,
                        tint = Color(0xFF4CAF50),
                        onClick = { movieNightEventViewModel.updateLikes() },
                        contentDescription = "Like"
                    )
                }

                Spacer(modifier = Modifier.weight(1.2f))
            }

            // --- LAYER 3: OVERLAYS ---
            if (movieNightEventUiState.showNewFriendDialog) {
                TurnConfirmationDialog(
                    friendName = movieNightEventUiState.currentFriend?.name,
                    friendIcon = movieNightEventUiState.currentFriend?.icon,
                    friendColor = movieNightEventUiState.currentFriend?.color,
                    onConfirm = { movieNightEventViewModel.closeNewFriendDialog() }
                )
            }

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
                        }
                    )
                }
            }
        }
    }
}

/**
 * Reusable helper for the round voting buttons to keep the main code clean.
 */
@Composable
fun VoteButton(
    icon: ImageVector,
    tint: Color,
    onClick: () -> Unit,
    contentDescription: String
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(80.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            tonalElevation = 2.dp
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier
                    .size(54.dp)
                    .padding(12.dp)
            )
        }
    }
}

/**
 * Extracted Dialog for better code organization
 */
@Composable
fun TurnConfirmationDialog(
    friendName: String?,
    friendIcon: ImageVector?,
    friendColor: Color?,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* Prevent dismissal by clicking outside */ },
        icon = {
            friendIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = friendColor ?: MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
            }
        },
        title = {
            Text(
                text = "It's ${friendName ?: "Someone"}'s Turn!",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Text(
                text = "Pass the phone to $friendName. Ready to vote?",
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("I'm Ready!")
            }
        }
    )
}