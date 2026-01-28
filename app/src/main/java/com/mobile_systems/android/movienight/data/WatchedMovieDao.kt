package com.mobile_systems.android.movienight.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchedMovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(watchedMovie: WatchedMovie)

    @Update
    suspend fun update(watchedMovie: WatchedMovie)

    @Delete
    suspend fun delete(watchedMovie: WatchedMovie)

    @Query("SELECT * FROM watchedMovies")
    fun getAllFavouriteMovies() : Flow<List<WatchedMovie>>

    @Query("SELECT * FROM watchedMovies WHERE id = :id")
    fun getFavouriteMovieById(id: String): Flow<WatchedMovie>
}