package com.mobile_systems.android.movienight.model

import androidx.annotation.StringDef
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Movie(
    @SerialName("imdbId")
    val id: String,
    @SerialName("short")
    val info: ShortData,
    @SerialName("top")
    val stats: TopData,
    val likes: Int = 0,
    val dislikes: Int = 0
) {
    @Serializable
    data class ShortData(
        val name: String,
        val description: String,
        val image: String,
        val genre: List<String>,
        @SerialName("actor")
        val actors: List<ActorInfo>,
        @SerialName("director")
        val directors: List<DirectorInfo>
    )

    @Serializable
    data class TopData(
        val runtime: RuntimeWrapper,
        val releaseDate: ReleaseDateInfo,
        @SerialName("ratingsSummary")
        val rating: RatingInfo
    )
}

// These helper classes are needed to reach the "fruit" on the branches
@Serializable data class ActorInfo(val name: String)
@Serializable data class DirectorInfo(val name: String)
@Serializable data class RatingInfo(val aggregateRating: Double)
@Serializable data class ReleaseDateInfo(val year: Int)
@Serializable data class RuntimeWrapper(val displayableProperty: DisplayableProperty)
@Serializable data class DisplayableProperty(val value: ValueWrapper)
@Serializable data class ValueWrapper(val plainText: String)