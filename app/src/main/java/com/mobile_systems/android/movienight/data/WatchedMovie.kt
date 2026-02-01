package com.mobile_systems.android.movienight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//Defines a table in the local database: watchedMovies and how each entity is defined
//Each instance represents a movie through its thumbnail (if available), and its id
@Entity(tableName = "watchedMovies")
data class WatchedMovie (
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val thumbnail: String? = null
)