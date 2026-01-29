package com.mobile_systems.android.movienight.ui

import Movie
import com.mobile_systems.android.movienight.data.WatchedMovie
import com.mobile_systems.android.movienight.data.MovieToWatch

data class MovieDetailsUiState(
    val id: String = "",
    val isToWatch: Boolean = false,
    val isWatched: Boolean = false,
    val selectedMovie: Movie = Movie(),
    val title: String? = null,
)

fun String.toFavouriteMovie() : WatchedMovie = WatchedMovie(
    id = this
)

fun String.toMovieToWatch() : MovieToWatch = MovieToWatch(
    id = this
)