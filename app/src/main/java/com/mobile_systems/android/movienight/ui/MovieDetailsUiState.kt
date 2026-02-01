package com.mobile_systems.android.movienight.ui

import Movie
import MovieDetails
import MovieResource
import com.mobile_systems.android.movienight.data.WatchedMovie
import com.mobile_systems.android.movienight.data.MovieToWatch
import kotlin.String

data class MovieDetailsUiState(
    val isSelected: Boolean = false,
    val isToWatch: Boolean = false,
    val isWatched: Boolean = false,
    val selectedMovie: MovieDetails = MovieDetails(),
)

fun MovieToWatch.toMovie() : Movie = Movie(
    thumbnail =  this.thumbnail,
    data = MovieResource(movieId = this.id),
)

fun MovieDetails.toMovieToWatch() : MovieToWatch = MovieToWatch(
    thumbnail = this.content?.thumbnail ?: "",
    id = this.movieId
)

fun MovieDetails.toWatchedMovie() : WatchedMovie = WatchedMovie(
    thumbnail = this.content?.thumbnail ?: "",
    id = this.movieId
)