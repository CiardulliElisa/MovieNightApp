package com.mobile_systems.android.movienight.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieFinder(
    val results: List<String>
)
