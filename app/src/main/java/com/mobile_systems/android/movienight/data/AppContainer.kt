package com.mobile_systems.android.movienight.data

import android.content.Context
import com.mobile_systems.android.movienight.data.network.MoviesApiService
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {
    val savedMoviesRepository: SavedMoviesRepository
    val moviesRepository : MoviesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val baseUrl = "https://imdb.iamidiotareyoutoo.com/"
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: MoviesApiService by lazy {
        retrofit.create(MoviesApiService::class.java)
    }

    /**
     * DI implementation for Mars photos repository
     */
    override val moviesRepository: MoviesRepository by lazy {
        NetworkMoviesRepository(retrofitService)
    }

    override val savedMoviesRepository : SavedMoviesRepository by lazy {
        OfflineSavedMoviesRepository(
            SavedMoviesDatabase.getDatabase(context).watchedMovieDao(),
            movieToWatchDao = SavedMoviesDatabase.getDatabase(context).movieToWatchDao()
        )
    }
}


