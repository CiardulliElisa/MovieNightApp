package com.mobile_systems.android.movienight.data.network

import Movie
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApiService {

        @GET("trailers")
        suspend fun getMoviesByGenre(
                @Query("genres") movieGenres: String,
                @Query("language") language: String = "en",
                @Query("page") limit: Int = 3
        ): Map<String, Movie>
        @GET("trailers")
        suspend fun getMovies(
                @Query("language") language: String = "en",
                @Query("page") page: Int = 5
        ) : Map<String, Movie>
}