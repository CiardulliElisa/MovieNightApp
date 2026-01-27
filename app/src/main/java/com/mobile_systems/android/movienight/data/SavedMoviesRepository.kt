package com.mobile_systems.android.movienight.data

import kotlinx.coroutines.flow.Flow

interface SavedMoviesRepository {
    suspend fun insertFavouriteMovie(favouriteMovie: FavouriteMovie)
    suspend fun updateFavouriteMovie(favouriteMovie: FavouriteMovie)
    suspend fun deleteFavouriteMovie(favouriteMovie: FavouriteMovie)
    fun getAllFavouriteMovies() : Flow<List<FavouriteMovie>>
    fun getFavouriteMovieById(id: String): Flow<FavouriteMovie>

    suspend fun insertMovieToWatch(movieToWatch: MovieToWatch)
    suspend fun updateMovieToWatch(movieToWatch: MovieToWatch)
    suspend fun deleteMovieToWatch(movieToWatch: MovieToWatch)
    fun getAllMoviesToWatch() : Flow<List<MovieToWatch>>
    fun getMovieToWatchById(id: String): Flow<MovieToWatch>

}
