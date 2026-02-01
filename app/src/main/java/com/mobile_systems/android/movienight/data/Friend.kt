package com.mobile_systems.android.movienight.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

//Simple class used throughout the movie night events, to define users participating in the session, by assigning them an icon, a color and a name
data class Friend(
    val icon: ImageVector,
    val color: Color,
    val name: String
)