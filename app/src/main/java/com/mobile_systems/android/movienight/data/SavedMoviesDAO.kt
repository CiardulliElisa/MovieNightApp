package com.mobile_systems.android.movienight.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//Provides the commands needed to interact with the local database
@Dao
interface SavedMoviesDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(watchedMovie: WatchedMovie)

    @Update
    suspend fun update(watchedMovie: WatchedMovie)

    @Delete
    suspend fun delete(watchedMovie: WatchedMovie)

    //Gets all the watched movies from the database
    @Query("SELECT * FROM watchedMovies")
    fun getAllWatchedMovies() : Flow<List<WatchedMovie>>

    //Gets a specific watched movie from the database
    @Query("SELECT * FROM watchedMovies WHERE id = :id")
    fun getWatchedMovieById(id: String): Flow<WatchedMovie?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movieToWatch: MovieToWatch)

    @Update
    suspend fun update(movieToWatch: MovieToWatch)

    @Delete
    suspend fun delete(movieToWatch: MovieToWatch)

    //Gets all the movies to watch from the database
    @Query("SELECT * FROM moviesToWatch")
    fun getAllMoviesToWatch() : Flow<List<MovieToWatch>>

    //Gets a specific movie to watch from the database
    @Query("SELECT * FROM moviesToWatch WHERE id = :id")
    fun getMovieToWatchById(id: String): Flow<MovieToWatch?>
}