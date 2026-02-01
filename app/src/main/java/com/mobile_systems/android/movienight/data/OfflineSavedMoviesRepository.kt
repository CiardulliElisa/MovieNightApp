package com.mobile_systems.android.movienight.data

import kotlinx.coroutines.flow.Flow

// A repository that handles the local database (offline) and all the operations that can be done there. It implements SavedMoviesRepository
class OfflineSavedMoviesRepository(private val savedMoviesDAO: SavedMoviesDAO) : SavedMoviesRepository {

    //Commands for the watched movies table

    override suspend fun insertWatchedMovie(watchedMovie: WatchedMovie) = savedMoviesDAO.insert(watchedMovie)

    override suspend fun updateWatchedMovie(watchedMovie: WatchedMovie) = savedMoviesDAO.update(watchedMovie)

    override suspend fun deleteWatchedMovie(watchedMovie: WatchedMovie) = savedMoviesDAO.delete(watchedMovie)

    override fun getAllWatchedMovies(): Flow<List<WatchedMovie>> = savedMoviesDAO.getAllWatchedMovies()

    override fun getWatchedMovieById(id: String): Flow<WatchedMovie?> = savedMoviesDAO.getWatchedMovieById(id)

    //Commands for the movies to watch table

    override suspend fun insertMovieToWatch(movieToWatch: MovieToWatch) = savedMoviesDAO.insert(movieToWatch)

    override suspend fun updateMovieToWatch(movieToWatch: MovieToWatch) = savedMoviesDAO.update(movieToWatch)

    override suspend fun deleteMovieToWatch(movieToWatch: MovieToWatch) = savedMoviesDAO.delete(movieToWatch)

    override fun getAllMoviesToWatch(): Flow<List<MovieToWatch>> = savedMoviesDAO.getAllMoviesToWatch()

    override fun getMovieToWatchById(id: String): Flow<MovieToWatch?> = savedMoviesDAO.getMovieToWatchById(id)

}