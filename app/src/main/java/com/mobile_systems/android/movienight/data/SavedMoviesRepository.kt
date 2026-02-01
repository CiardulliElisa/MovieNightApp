package com.mobile_systems.android.movienight.data

import kotlinx.coroutines.flow.Flow

//Defines all possible operations that can be performed in the local database: savedMovies
interface SavedMoviesRepository {
    suspend fun insertWatchedMovie(watchedMovie: WatchedMovie)
    suspend fun updateWatchedMovie(watchedMovie: WatchedMovie)
    suspend fun deleteWatchedMovie(watchedMovie: WatchedMovie)
    fun getAllWatchedMovies() : Flow<List<WatchedMovie>>
    fun getWatchedMovieById(id: String): Flow<WatchedMovie?>

    suspend fun insertMovieToWatch(movieToWatch: MovieToWatch)
    suspend fun updateMovieToWatch(movieToWatch: MovieToWatch)
    suspend fun deleteMovieToWatch(movieToWatch: MovieToWatch)
    fun getAllMoviesToWatch() : Flow<List<MovieToWatch>>
    fun getMovieToWatchById(id: String): Flow<MovieToWatch?>

}
