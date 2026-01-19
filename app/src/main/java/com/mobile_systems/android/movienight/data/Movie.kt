package com.mobile_systems.android.movienight.data

data class Movie(
    val id: String,
    val title: String,
    val description: String,
    val rating: Double,
    val posterUrl: String,
    val genre: List<String>,
    val actors: List<String>,
    val runtime: String,
    val director: String,
    val releaseDate: String,
)