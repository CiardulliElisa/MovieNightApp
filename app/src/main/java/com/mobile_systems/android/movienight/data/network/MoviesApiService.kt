package com.mobile_systems.android.movienight.data.network

import com.mobile_systems.android.movienight.model.Movie
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesApiService {

        /**
         * Returns a [List] of [Movie] and this method can be called from a Coroutine.
         * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
         * HTTP method
         */
        @GET("search")
        suspend fun getMovie(@Query("tt") movieTtId: String): Movie
}