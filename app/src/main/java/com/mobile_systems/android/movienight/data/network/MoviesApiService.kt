package com.mobile_systems.android.movienight.data.network

import Movie
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

        @GET("trailers")
        suspend fun getMovieTitle(
                @Query("language") language: String = "en",
                @Query("imdb_id") movieId: String
        )
}