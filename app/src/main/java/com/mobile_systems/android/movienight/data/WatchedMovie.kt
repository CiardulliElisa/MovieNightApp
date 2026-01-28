package com.mobile_systems.android.movienight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchedMovies")
data class WatchedMovie (
    @PrimaryKey(autoGenerate = false)
    val id: String,
)