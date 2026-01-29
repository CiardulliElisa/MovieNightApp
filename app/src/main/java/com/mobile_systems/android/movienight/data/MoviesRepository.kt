package com.mobile_systems.android.movienight.data

import Movie
import com.mobile_systems.android.movienight.data.network.MoviesApiService
import com.mobile_systems.android.movienight.model.MovieDetails
import kotlinx.serialization.json.Json

interface MoviesRepository {
    // Change from List<String> to List<MovieFinder>
    suspend fun getMoviesByGenre(genres: String, limit: Int): List<Movie>
    suspend fun getMovies(count: Int): List<Movie>

    suspend fun getMovieDetails(movieId: String) : MovieDetails
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

    override suspend fun getMovieDetails(movieId: String): MovieDetails {
        return try {
            kinoCheckRetrofitService.getMovieDetails(movieId)
        } catch (e: Exception) {
            android.util.Log.e("REPO_ERROR", "Mapping failed", e)
            MovieDetails(title = "Error Loading Details")
        }
    }
}