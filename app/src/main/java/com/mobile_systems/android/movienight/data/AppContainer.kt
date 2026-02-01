package com.mobile_systems.android.movienight.data

import android.content.Context
import com.mobile_systems.android.movienight.data.network.MoviesApiService
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import retrofit2.converter.kotlinx.serialization.asConverterFactory

//Defines all available data sources in the application
interface AppContainer {
    val savedMoviesRepository: SavedMoviesRepository
    val moviesRepository : MoviesRepository
}

//Sets up all repositories in the application, so they are not built more than once
class AppDataContainer(private val context: Context) : AppContainer {

    private val kinoCheckUrl = "https://api.kinocheck.com/"
    private val json = Json {
        ignoreUnknownKeys = true
    }

    //Configures Retrofit to handle network communication and JSON parsing.
    private val kinoCheckRetrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(kinoCheckUrl)
        .build()

    //Creates the connection to the MoviesApiService
    private val kinoCheckRetrofitService: MoviesApiService by lazy {
        kinoCheckRetrofit.create(MoviesApiService::class.java)
    }

    //Creates the repository that is used to fetch data from the APIs
    override val moviesRepository: MoviesRepository by lazy {
        NetworkMoviesRepository( kinoCheckRetrofitService)
    }

    //The repository that is used to fetch data from the local offline database
    override val savedMoviesRepository : SavedMoviesRepository by lazy {
        OfflineSavedMoviesRepository(
            savedMoviesDAO = SavedMoviesDatabase.getDatabase(context).savedMoviesDAO()
        )
    }
}


