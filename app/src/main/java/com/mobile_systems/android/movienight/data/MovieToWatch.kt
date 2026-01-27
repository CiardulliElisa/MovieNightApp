package com.mobile_systems.android.movienight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moviesToWatch")
data class MovieToWatch (
    @PrimaryKey(autoGenerate = false)
    val id: String,
)