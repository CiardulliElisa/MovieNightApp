package com.mobile_systems.android.movienight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favouriteMovies")
data class FavouriteMovie (
    @PrimaryKey(autoGenerate = false)
    val id: String,
)