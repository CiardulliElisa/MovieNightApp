package com.mobile_systems.android.movienight.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieDetails(
    val id: String = "",
    val title: String = "",
    val trailer: TrailerInfo? = null
)

@Serializable
data class TrailerInfo(
    val thumbnail: String = "",
    val genres: List<String> = emptyList()
)
