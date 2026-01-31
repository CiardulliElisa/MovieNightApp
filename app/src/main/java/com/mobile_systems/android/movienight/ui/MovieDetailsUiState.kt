package com.mobile_systems.android.movienight.ui

import Movie
import MovieResource
import com.mobile_systems.android.movienight.data.WatchedMovie
import com.mobile_systems.android.movienight.data.MovieToWatch
import com.mobile_systems.android.movienight.model.MovieDetails
import kotlinx.serialization.Serializable
import kotlin.String

data class MovieDetailsUiState(
    val id: String = "",
    val isToWatch: Boolean = false,
    val isWatched: Boolean = false,
    val selectedMovie: MovieDetails = MovieDetails(),
)

fun Movie.toWatchedMovie() : WatchedMovie = WatchedMovie(
    thumbnail =  this.thumbnail,
    id = this.data.movieId,
)

fun Movie.toMovieToWatch() : MovieToWatch = MovieToWatch(
    thumbnail =  this.thumbnail,
    id = this.data.movieId,
)

fun MovieToWatch.toMovie() : Movie = Movie(
    thumbnail =  this.thumbnail,
    data = MovieResource(movieId = this.id),
)

fun MovieDetails.toMovie() : Movie = Movie(
    thumbnail = this.trailer?.thumbnail ?: "",
    likes = 0,
    dislikes = 0,
    data = MovieResource(movieId = this.id)

)

@Serializable
data class MovieDetails(
    val id: String = "",
    val title: String = "",
    val trailer: TrailerInfo? = null
)

@Serializable
data class TrailerInfo(
    val thumbnail: String = "",
    val genres: List<String> = emptyList()
)