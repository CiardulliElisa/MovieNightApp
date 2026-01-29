package com.mobile_systems.android.movienight.data

import Movie
import android.os.Build
import android.util.Log
import com.mobile_systems.android.movienight.data.network.MoviesApiService
import java.time.ZonedDateTime
import java.time.format.DateTimeParseException

interface MoviesRepository {
    // Change from List<String> to List<MovieFinder>
    suspend fun getMoviesByGenre(genres: String, limit: Int): List<Movie>

    suspend fun getMovies(count: Int): List<Movie>
}

class NetworkMoviesRepository(
    private val kinoCheckRetrofitService: MoviesApiService
) : MoviesRepository {

    override suspend fun getMoviesByGenre(genres: String, limit : Int): List<Movie> {
        return try {
            val randomPage = (5..100).random()
            val responseMap = kinoCheckRetrofitService.getMoviesByGenre(genres, randomPage, limit)

            responseMap.values.take(limit).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovies(count: Int): List<Movie> {
        return try {
            val randomPage = (5..100).random()
            val responseMap = kinoCheckRetrofitService.getMovies(randomPage)
            responseMap.values.take(count).toList()

        } catch (e: Exception) {
            emptyList()
        }
    }
}