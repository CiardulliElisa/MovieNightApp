package com.mobile_systems.android.movienight.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mobile_systems.android.movienight.R

@Composable
fun MovieDetailsCard(
    onClose: () -> Unit
) {
    OutlinedCard(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .fillMaxHeight(0.85f)
    ) {
        // We use a Box here so we can stack the Close Icon on top of the Poster
        Box(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                // 1. The Poster Card
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(350.dp),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.littlemisssunshinejpg),
                        contentDescription = "Poster",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                }

                // 2. The Text Content
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                    Text(
                        text = "Little Miss Sunshine",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center, // Centers the text lines if they wrap
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)// Makes the text take up the full width so it can be centered
                    )

                    Text(
                        text = "A family determines to get their young daughter into the finals of a beauty pageant.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp)
                    )

                    DetailSection(label = "Actors", value = "Steve Carell, Toni Collette")
                    DetailSection(label = "Genres", value = "Drama, Comedy")
                    DetailSection(label = "Rating", value = "⭐ 8.5")
                }
            }

            // 3. The Close Icon Button (Top Right)
            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .align(Alignment.TopEnd) // Positions it in the corner
                    .padding(24.dp) // Aligns it nicely relative to the poster card
            ) {
                // Using a Surface/CircleShape to make the icon readable against the poster
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    tonalElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.padding(8.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// Helper component for the "Label: Value" rows
@Composable
fun DetailSection(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}