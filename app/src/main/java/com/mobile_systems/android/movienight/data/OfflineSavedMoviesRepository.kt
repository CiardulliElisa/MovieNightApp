package com.mobile_systems.android.movienight.data

import kotlinx.coroutines.flow.Flow

class OfflineSavedMoviesRepository(private val savedMoviesDAO: SavedMoviesDAO) : SavedMoviesRepository {

    override suspend fun insertWatchedMovie(watchedMovie: WatchedMovie) = savedMoviesDAO.insert(watchedMovie)

    override suspend fun updateWatchedMovie(watchedMovie: WatchedMovie) = savedMoviesDAO.update(watchedMovie)

    override suspend fun deleteWatchedMovie(watchedMovie: WatchedMovie) = savedMoviesDAO.delete(watchedMovie)

    override fun getAllWatchedMovies(): Flow<List<WatchedMovie>> = savedMoviesDAO.getAllFavouriteMovies()

    override fun getWatchedMovieById(id: String): Flow<WatchedMovie?> = savedMoviesDAO.getFavouriteMovieById(id)

    override suspend fun insertMovieToWatch(movieToWatch: MovieToWatch) = savedMoviesDAO.insert(movieToWatch)

    override suspend fun updateMovieToWatch(movieToWatch: MovieToWatch) = savedMoviesDAO.update(movieToWatch)

    override suspend fun deleteMovieToWatch(movieToWatch: MovieToWatch) = savedMoviesDAO.delete(movieToWatch)

    override fun getAllMoviesToWatch(): Flow<List<MovieToWatch>> = savedMoviesDAO.getAllMoviesToWatch()

    override fun getMovieToWatchById(id: String): Flow<MovieToWatch?> = savedMoviesDAO.getMovieToWatchById(id)

}