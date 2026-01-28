package com.mobile_systems.android.movienight.data

import kotlinx.coroutines.flow.Flow

class OfflineSavedMoviesRepository(private val watchedMovieDao: WatchedMovieDao, private val movieToWatchDao: MovieToWatchDao) : SavedMoviesRepository {

    override suspend fun insertWatchedMovie(watchedMovie: WatchedMovie) = watchedMovieDao.insert(watchedMovie)

    override suspend fun updateWatchedMovie(watchedMovie: WatchedMovie) = watchedMovieDao.update(watchedMovie)

    override suspend fun deleteWatchedMovie(watchedMovie: WatchedMovie) = watchedMovieDao.delete(watchedMovie)

    override fun getAllWatchedMovies(): Flow<List<WatchedMovie>> = watchedMovieDao.getAllFavouriteMovies()

    override fun getWatchedMovieById(id: String): Flow<WatchedMovie> = watchedMovieDao.getFavouriteMovieById(id)

    override suspend fun insertMovieToWatch(movieToWatch: MovieToWatch) = movieToWatchDao.insert(movieToWatch)

    override suspend fun updateMovieToWatch(movieToWatch: MovieToWatch) = movieToWatchDao.update(movieToWatch)

    override suspend fun deleteMovieToWatch(movieToWatch: MovieToWatch) = movieToWatchDao.delete(movieToWatch)

    override fun getAllMoviesToWatch(): Flow<List<MovieToWatch>> = movieToWatchDao.getAllMoviesToWatch()

    override fun getMovieToWatchById(id: String): Flow<MovieToWatch> = movieToWatchDao.getMovieToWatchById(id)

}