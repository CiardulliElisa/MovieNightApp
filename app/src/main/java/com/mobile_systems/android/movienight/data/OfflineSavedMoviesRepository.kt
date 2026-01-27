package com.mobile_systems.android.movienight.data

import kotlinx.coroutines.flow.Flow

class OfflineSavedMoviesRepository(private val favouriteMovieDao: FavouriteMovieDao, private val movieToWatchDao: MovieToWatchDao) : SavedMoviesRepository {

    override suspend fun insertFavouriteMovie(favouriteMovie: FavouriteMovie) = favouriteMovieDao.insert(favouriteMovie)

    override suspend fun updateFavouriteMovie(favouriteMovie: FavouriteMovie) = favouriteMovieDao.update(favouriteMovie)

    override suspend fun deleteFavouriteMovie(favouriteMovie: FavouriteMovie) = favouriteMovieDao.delete(favouriteMovie)

    override fun getAllFavouriteMovies(): Flow<List<FavouriteMovie>> = favouriteMovieDao.getAllFavouriteMovies()

    override fun getFavouriteMovieById(id: String): Flow<FavouriteMovie> = favouriteMovieDao.getFavouriteMovieById(id)

    override suspend fun insertMovieToWatch(movieToWatch: MovieToWatch) = movieToWatchDao.insert(movieToWatch)

    override suspend fun updateMovieToWatch(movieToWatch: MovieToWatch) = movieToWatchDao.update(movieToWatch)

    override suspend fun deleteMovieToWatch(movieToWatch: MovieToWatch) = movieToWatchDao.delete(movieToWatch)

    override fun getAllMoviesToWatch(): Flow<List<MovieToWatch>> = movieToWatchDao.getAllMoviesToWatch()

    override fun getMovieToWatchById(id: String): Flow<MovieToWatch> = movieToWatchDao.getMovieToWatchById(id)

}