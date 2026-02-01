package com.mobile_systems.android.movienight.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//t lists which tables exist and which version the database is currently on.
@Database(entities = [WatchedMovie::class, MovieToWatch::class], version = 6, exportSchema = false)
abstract class SavedMoviesDatabase : RoomDatabase() {

    //Connect the database to the commands defined in the DAO
    abstract fun savedMoviesDAO(): SavedMoviesDAO

    //Makes sure the database is only created once and accessible from the entire application
    companion object {
        @Volatile
        private var Instance: SavedMoviesDatabase? = null

        //This returns the database, and creates one if there is none
        fun getDatabase(context : Context) : SavedMoviesDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, SavedMoviesDatabase::class.java, "saved_movies_database")
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}