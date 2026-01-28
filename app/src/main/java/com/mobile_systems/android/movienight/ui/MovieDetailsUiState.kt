package com.mobile_systems.android.movienight.ui

import com.mobile_systems.android.movienight.data.WatchedMovie
import com.mobile_systems.android.movienight.model.Movie
import com.mobile_systems.android.movienight.data.MovieToWatch

data class MovieDetailsUiState(
    val id: String = "",
    val isFavourite: Boolean = false,
    val isWatched: Boolean = false,
    val movies : List<Movie> = listOf()
)

fun String.toFavouriteMovie() : WatchedMovie = WatchedMovie(
    id = this
)

fun String.toMovieToWatch() : MovieToWatch = MovieToWatch(
    id = this
)