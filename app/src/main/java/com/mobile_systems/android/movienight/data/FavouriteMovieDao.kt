package com.mobile_systems.android.movienight.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteMovieDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(favouriteMovie: FavouriteMovie)

    @Update
    suspend fun update(favouriteMovie: FavouriteMovie)

    @Delete
    suspend fun delete(favouriteMovie: FavouriteMovie)

    @Query("SELECT * FROM favouriteMovies")
    fun getAllFavouriteMovies() : Flow<List<FavouriteMovie>>

    @Query("SELECT * FROM favouriteMovies WHERE id = :id")
    fun getFavouriteMovieById(id: String): Flow<FavouriteMovie>
}