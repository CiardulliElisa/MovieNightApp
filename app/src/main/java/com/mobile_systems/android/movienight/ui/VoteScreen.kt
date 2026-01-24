package com.mobile_systems.android.movienight.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue


@Composable
fun VoteScreen(
    movieNightEventViewModel: MovieNightEventViewModel,
    modifier: Modifier = Modifier
) {
    val movieNightEventUiState by movieNightEventViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. TOP SPRING: Pushes everything towards the center
        Spacer(modifier = Modifier.weight(1f))

        // 2. MOVIE POSTER
        Box(
            modifier = Modifier.wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(300.dp, 450.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                //Will contain the clickable poster for the movie
                Text("Poster")
            }
        }

        // 3. FIXED GAP: Space between poster and buttons
        Spacer(modifier = Modifier.height(40.dp))

        // 4. BUTTONS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Dislike Button
            Button(
                onClick = { /* TODO */ },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.size(70.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Dislike", tint = Color.Red)
            }

            // Like Button
            Button(
                onClick = { /* TODO */ },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier.size(70.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = "Like", tint = Color(0xFF4CAF50))
            }
        }

        // 5. BOTTOM SPRING: Matches the top spring to ensure perfect centering
        Spacer(modifier = Modifier.weight(1f))
    }
}