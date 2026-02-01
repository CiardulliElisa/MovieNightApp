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

    @Query("SELECT * FROM watchedMovies")
    fun getAllFavouriteMovies() : Flow<List<WatchedMovie>>

    @Query("SELECT * FROM watchedMovies WHERE id = :id")
    fun getFavouriteMovieById(id: String): Flow<WatchedMovie?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movieToWatch: MovieToWatch)

    @Update
    suspend fun update(movieToWatch: MovieToWatch)

    @Delete
    suspend fun delete(movieToWatch: MovieToWatch)

    @Query("SELECT * FROM moviesToWatch")
    fun getAllMoviesToWatch() : Flow<List<MovieToWatch>>

    @Query("SELECT * FROM moviesToWatch WHERE id = :id")
    fun getMovieToWatchById(id: String): Flow<MovieToWatch?>
}