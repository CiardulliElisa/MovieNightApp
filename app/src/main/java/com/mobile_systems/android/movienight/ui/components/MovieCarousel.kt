package com.mobile_systems.android.movienight.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import com.mobile_systems.android.movienight.R

// 1. Data Models
data class CarouselItem(
    val id: Int,
    val imageResId: Int,
    val title: String,
    val description: String
)

val categories = listOf("Trending Now", "Watchlist", "Action Movies", "Comedy Hits")

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val searchState = rememberTextFieldState()

    // Use Box to layer the Floating Action Button on top of the content
    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {
            // Top Bar: Search and Settings
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MovieSearchBar(
                    textFieldState = searchState,
                    onSearch = { query -> println(query) },
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { /* Open Settings */ }) {
                    Icon(
                        imageVector = Icons.Default.DarkMode,
                        contentDescription = "Dark Mode",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Scrollable Movie Categories
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                for (category in categories) {
                    MovieCarousel(title = category)
                }
            }
        }

        // Animated Movie Night Button (Bottom Right)
        MovieNightButton(
            onClick = { /* Handle Movie Night Logic */ },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCarousel(title: String) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedMovie by remember { mutableStateOf<CarouselItem?>(null) }

    // Dummy data for posters
    val carouselItems = remember {
        listOf(
            CarouselItem(0, R.drawable.littlemisssunshinejpg, "Little Miss Sunshine", "A family determines to get their young daughter into the finals of a beauty pageant."),
            CarouselItem(1, R.drawable.littlemisssunshinejpg, "Movie Two", "Description for movie two goes here."),
            CarouselItem(2, R.drawable.littlemisssunshinejpg, "Movie Three", "Description for movie three goes here."),
            CarouselItem(3, R.drawable.littlemisssunshinejpg, "Movie Four", "Description for movie four goes here.")
        )
    }

    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, top = 8.dp)
        )

        HorizontalUncontainedCarousel(
            state = rememberCarouselState { carouselItems.count() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            itemWidth = 120.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { index ->
            val item = carouselItems[index]

            // Clickable Poster
            Surface(
                modifier = Modifier.height(180.dp),
                shape = MaterialTheme.shapes.extraLarge,
                onClick = {
                    selectedMovie = item
                    showDialog = true
                }
            ) {
                Image(
                    painter = painterResource(id = item.imageResId),
                    contentDescription = item.title,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // --- THE POP-UP SECTION ---
    if (showDialog && selectedMovie != null) {
        Dialog(
            onDismissRequest = { showDialog = false }
        ) {
            MovieDetailsCard(
                onClose = { showDialog = false },
            )
        }
    }
}