package com.mobile_systems.android.movienight.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [WatchedMovie::class, MovieToWatch::class], version = 1, exportSchema = false)
abstract class SavedMoviesDatabase : RoomDatabase() {

    abstract fun watchedMovieDao(): WatchedMovieDao
    abstract fun movieToWatchDao(): MovieToWatchDao

    companion object {
        @Volatile
        private var Instance: SavedMoviesDatabase? = null
        fun getDatabase(context : Context) : SavedMoviesDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SavedMoviesDatabase::class.java, "saved_movies_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}