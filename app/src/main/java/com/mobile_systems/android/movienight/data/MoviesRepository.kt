package com.mobile_systems.android.movienight.data

import Movie
import MovieDetails
import com.mobile_systems.android.movienight.data.network.MoviesApiService

interface MoviesRepository {
    suspend fun getMoviesByGenre(genres: String, limit: Int): List<Movie>
    suspend fun getMovies(count: Int): List<Movie>
    suspend fun getMovieDetails(movieId: String) : MovieDetails
}

// This class handles the fetching of data from an API.
//It implements the above MoviesRepository
class NetworkMoviesRepository(
    private val kinoCheckRetrofitService: MoviesApiService
) : MoviesRepository {

    //Gets a list of movies from the API, based on a chosen genre, if an error occurs it returns an empty list
    override suspend fun getMoviesByGenre(genres: String, limit: Int): List<Movie> {
        return try {
            val randomPage = (5..100).random()
            val responseMap = kinoCheckRetrofitService.getMoviesByGenre(genres, randomPage, limit)
            responseMap.values.take(limit).toList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    //Gets a list of a selected size of random movies from the API, if an error occurs it returns an empty list
    override suspend fun getMovies(count: Int): List<Movie> {

        val movieList = mutableListOf<Movie>()

        //Keep track of attempts to not get stuck in an infinite loop.
        var attempts = 0
        val maxAttempts = count * 2

        //Get movies until either the list is filled, or there are no more available attempts
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

    //Gets the details of a chosen movie from the API, if an error occurs it returns an empty MovieDetails object
    override suspend fun getMovieDetails(movieId: String): MovieDetails {
        return try {
            kinoCheckRetrofitService.getMovieDetails(movieId)
        } catch (e: Exception) {
            MovieDetails()
        }
    }
}