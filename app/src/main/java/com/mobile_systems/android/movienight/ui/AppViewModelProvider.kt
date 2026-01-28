package com.mobile_systems.android.movienight.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mobile_systems.android.movienight.MovieNightApplication
import com.mobile_systems.android.movienight.ui.home.HomeViewModel
import com.mobile_systems.android.movienight.ui.movienightevent.MovieNightEventViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            MovieViewModel(
                moviesRepository = movieNightApplication().container.moviesRepository
            )
        }

        initializer {
            HomeViewModel(
                this.createSavedStateHandle()
            )
        }
        initializer {
            MovieDetailsViewModel(
                movieNightApplication().container.savedMoviesRepository
            )
        }

        initializer {
            MovieNightEventViewModel(
                moviesRepository = movieNightApplication().container.moviesRepository
            )
        }
    }
}

fun CreationExtras.movieNightApplication(): MovieNightApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MovieNightApplication)
