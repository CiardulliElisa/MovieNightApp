package com.mobile_systems.android.movienight.data.network

import Movie
import com.mobile_systems.android.movienight.model.MovieDetails
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApiService {

        @GET("trailers")
        suspend fun getMoviesByGenre(
                @Query("genres") movieGenres: String,
                @Query("page") page: Int,
                @Query("limit") limit: Int,
                @Query("language") language: String = "en",
        ): Map<String, Movie>
        @GET("trailers")
        suspend fun getMovies(
                @Query("page") page: Int,
                @Query("language") language: String = "en"
        ) : Map<String, Movie>

        @GET("movies")
        suspend fun getMovieDetails(
                @Query("imdb_id") movieId: String,
                @Query("language") language: String = "en"
        ) : MovieDetails
}