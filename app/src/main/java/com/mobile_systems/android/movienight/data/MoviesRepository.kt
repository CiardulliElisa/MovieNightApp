package com.mobile_systems.android.movienight.data

import Movie
import MovieDetails
import com.mobile_systems.android.movienight.data.network.MoviesApiService

interface MoviesRepository {
    suspend fun getMoviesByGenre(genres: String, limit: Int): List<Movie>
    suspend fun getMovies(count: Int): List<Movie>
    suspend fun getMovieDetails(movieId: String) : MovieDetails
}

class NetworkMoviesRepository(
    private val kinoCheckRetrofitService: MoviesApiService
) : MoviesRepository {

    override suspend fun getMoviesByGenre(genres: String, limit: Int): List<Movie> {
        return try {
            val randomPage = (5..100).random()
            val responseMap = kinoCheckRetrofitService.getMoviesByGenre(genres, randomPage, limit)
            responseMap.values.take(limit).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMovies(count: Int): List<Movie> {

        val movieList = mutableListOf<Movie>()
        var attempts = 0
        val maxAttempts = count * 2

        while (movieList.size < count && attempts < maxAttempts) {
            attempts++
            try {
                val randomPage = (1..500).random()
                val responseMap = kinoCheckRetrofitService.getMovie(randomPage)
                val movie = responseMap.values.firstOrNull()
                if (movie != null && movieList.none { it.data.movieId == movie.data.movieId }) {
                    movieList.add(movie)
                }
            } catch (e: Exception) {
            }
        }
        return movieList
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