package com.mobile_systems.android.movienight.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mobile_systems.android.movienight.MovieNightApplication
import com.mobile_systems.android.movienight.ui.home.HomeViewModel
import com.mobile_systems.android.movienight.ui.home.MovieListsViewModel
import com.mobile_systems.android.movienight.ui.movienightevent.MovieNightEventViewModel

/**
 * AppViewModelProvider centralizes the creation of all view models in the app.
 * It makes sure the view models are created only once and use the correct repositories defined in the container.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {

        initializer {
            MovieListsViewModel(
                moviesRepository = movieNightApplication().container.moviesRepository
            )
        }

        initializer {
            HomeViewModel(
                savedMoviesRepository = movieNightApplication().container.savedMoviesRepository
            )
        }
        initializer {
            MovieDetailsViewModel(
                movieNightApplication().container.savedMoviesRepository,
                moviesRepository = movieNightApplication().container.moviesRepository
            )
        }

        initializer {
            MovieNightEventViewModel(
                moviesRepository = movieNightApplication().container.moviesRepository
            )
        }
    }
}

/** Accesses the custom Application where the container was created */
fun CreationExtras.movieNightApplication(): MovieNightApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as MovieNightApplication)
