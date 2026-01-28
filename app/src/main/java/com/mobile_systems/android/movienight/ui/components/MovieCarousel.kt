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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.mobile_systems.android.movienight.R

// 1. Data Models
data class CarouselItem(
    val id: String,
    val imageResId: Int,
    val title: String,
    val description: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCarousel(
    title: String,
    onMovieClick: (CarouselItem) -> Unit
) {
    // Your original dummy data
    val carouselItems = remember {
        listOf(
            CarouselItem("0", R.drawable.littlemisssunshinejpg, "Little Miss Sunshine", "A family determines to get their young daughter into the finals of a beauty pageant."),
            CarouselItem("2", R.drawable.littlemisssunshinejpg, "Movie Two", "Description for movie two goes here."),
            CarouselItem("3", R.drawable.littlemisssunshinejpg, "Movie Three", "Description for movie three goes here."),
            CarouselItem("6", R.drawable.littlemisssunshinejpg, "Movie Four", "Description for movie four goes here.")
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

            Surface(
                modifier = Modifier.height(180.dp),
                shape = MaterialTheme.shapes.extraLarge,
                onClick = { onMovieClick(item) }
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
}