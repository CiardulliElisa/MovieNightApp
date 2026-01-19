package com.mobile_systems.android.movienight.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mobile_systems.android.movienight.ui.components.MovieCarousel

@Composable
fun HomeScreen(modifier: Modifier) {
    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Featured Treats",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        MovieCarousel()
    }
}