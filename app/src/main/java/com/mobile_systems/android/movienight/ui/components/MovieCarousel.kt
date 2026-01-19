package com.mobile_systems.android.movienight.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mobile_systems.android.movienight.R
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieCarousel(title: String) {


    //The items in the carousel are the movies
    data class CarouselItem(
        val id: Int,
        @DrawableRes val imageResId: Int,
        val contentDescription: String
    )

    val carouselItems = remember {
        listOf(
            CarouselItem(0, R.drawable.littlemisssunshinejpg, "poster"),
            CarouselItem(1, R.drawable.littlemisssunshinejpg, "poster"),
            CarouselItem(2, R.drawable.littlemisssunshinejpg, "poster"),
            CarouselItem(3, R.drawable.littlemisssunshinejpg, "poster"),
            CarouselItem(4, R.drawable.littlemisssunshinejpg, "poster"),
        )
    }

    Column {

        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(start = 16.dp) // Aligns with the screen edge
        )

        HorizontalUncontainedCarousel(
            state = rememberCarouselState { carouselItems.count() },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 4.dp, bottom = 16.dp),
            itemWidth = 120.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { i ->
            val item = carouselItems[i]
            Image(
                modifier = Modifier
                    .height(180.dp)
                    .maskClip(MaterialTheme.shapes.extraLarge),
                painter = painterResource(id = item.imageResId),
                contentDescription = item.contentDescription,
                contentScale = ContentScale.FillBounds
            )
        }
    }


}