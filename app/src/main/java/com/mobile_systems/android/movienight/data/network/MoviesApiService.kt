package com.mobile_systems.android.movienight.data.network

import Movie
import MovieDetails
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApiService {

        //Gets a list of movies based on a chosen genre
        @GET("trailers")
        suspend fun getMoviesByGenre(
                @Query("genres") movieGenres: String,
                @Query("page") page: Int,
                @Query("limit") limit : Int,
                @Query("language") language: String = "en",
        ): Map<String, Movie>

        //Gets a movie from a chosen page
        @GET("trailers")
        suspend fun getMovie(
                @Query("page") page: Int,
                @Query("language") language: String = "en",
                @Query("limit") limit: Int = 1
        ) : Map<String, Movie>

        //Gets additional information for a specified movie
        @GET("movies")
        suspend fun getMovieDetails(
                @Query("imdb_id") movieId: String,
                @Query("language") language: String = "en"
        ) : MovieDetails
}