package com.mobile_systems.android.movienight.data

import android.content.Context


interface AppContainer {
    val savedMoviesRepository: SavedMoviesRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val savedMoviesRepository : SavedMoviesRepository by lazy {
        OfflineSavedMoviesRepository(
            SavedMoviesDatabase.getDatabase(context).watchedMovieDao(),
            movieToWatchDao = SavedMoviesDatabase.getDatabase(context).movieToWatchDao()
        )
    }
}


