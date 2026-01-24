package com.mobile_systems.android.movienight.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun VoteScreen(
    movieNightEventViewModel: MovieNightEventViewModel,
    modifier: Modifier = Modifier,
    onMovieNightFinished: () -> Unit
) {
    val uiState by movieNightEventViewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isMovieNightFinished) {
        if (uiState.isMovieNightFinished) {
            onMovieNightFinished()
        }
    }

    if (uiState.showNewFriendDialog) {
        AlertDialog(
            onDismissRequest = {
                movieNightEventViewModel.closeNewFriendDialog()
            },
            icon = {
                uiState.currentFriend?.let { friend ->
                    Icon(
                        imageVector = friend.icon,
                        contentDescription = null,
                        tint = friend.color,
                        modifier = Modifier.size(64.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "It's ${uiState.currentFriend?.name ?: "Someone"}'s Turn!",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Pass the phone to ${uiState.currentFriend?.name}. It's time to vote on the next movie!",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { movieNightEventViewModel.closeNewFriendDialog() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("I'm Ready!")
                }
            }
        )
    }

    // --- MAIN SCREEN CONTENT ---
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. TOP SPRING
        Spacer(modifier = Modifier.weight(1f))

        // 2. MOVIE POSTER CARD
        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(300.dp, 450.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.currentMovie ?: "No Movie Selected",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }

        // 3. FIXED GAP
        Spacer(modifier = Modifier.height(40.dp))

        // 4. VOTING BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dislike Button
            IconButton(
                onClick = { movieNightEventViewModel.updateCurrentMovie() },
                modifier = Modifier.size(80.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dislike",
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp).padding(8.dp)
                    )
                }
            }

            // Like Button
            IconButton(
                onClick = { movieNightEventViewModel.updateCurrentMovie() },
                modifier = Modifier.size(80.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Like",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp).padding(8.dp)
                    )
                }
            }
        }

        // 5. BOTTOM SPRING
        Spacer(modifier = Modifier.weight(1f))
    }
}