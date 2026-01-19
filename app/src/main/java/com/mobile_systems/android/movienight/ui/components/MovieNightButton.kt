package com.mobile_systems.android.movienight.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun MovieNightButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "periodicPulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1f, // We return to 1f at the end of the keyframe
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 5000 // Total loop duration (5 seconds)

                // First Pulse
                1.0f at 0 using FastOutSlowInEasing
                1.15f at 400
                1.0f at 800

                // Second Pulse
                1.15f at 1200
                1.0f at 1600

                // Static period
                1.0f at 5000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )

    ExtendedFloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = modifier
            .height(64.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        icon = { Icon(Icons.Filled.Movie, null) },
        text = { Text(text = "Movie Night!", style = MaterialTheme.typography.titleLarge) },
    )
}