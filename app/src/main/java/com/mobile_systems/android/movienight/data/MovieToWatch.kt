package com.mobile_systems.android.movienight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

//Defines a table in the local database: moviesToWatch and how each entity is defined
//Each instance represents a movie through its thumbnail (if available), and its id
@Entity(tableName = "moviesToWatch")
data class MovieToWatch (
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val thumbnail: String? = null,
)