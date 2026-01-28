package com.mobile_systems.android.movienight.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile_systems.android.movienight.data.SavedMoviesRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val savedMoviesRepository: SavedMoviesRepository
) : ViewModel() {

    /**
     * Holds current movie ui state, mimicking ItemEditViewModel's pattern
     */
    var movieUiState by mutableStateOf(MovieDetailsUiState())
        private set

    /**
     * Logic similar to the init block in ItemEditViewModel.
     * It fetches the current database status for the selected ID.
     */
    fun selectMovie(movieId: String) {
        viewModelScope.launch {
            val toWatchEntry = savedMoviesRepository.getMovieToWatchById(movieId).firstOrNull()
            val watchedEntry = savedMoviesRepository.getWatchedMovieById(movieId).firstOrNull()

            // Updating the state directly using the setter
            movieUiState = MovieDetailsUiState(
                id = movieId,
                isFavourite = toWatchEntry != null,
                isWatched = watchedEntry != null
            )
        }
    }

    /**
     * Toggles the "To Watch" status in the repository
     */
    fun toggleToWatch() {
        viewModelScope.launch {
            if (movieUiState.isFavourite) {
                savedMoviesRepository.deleteMovieToWatch(movieUiState.id.toMovieToWatch())
            } else {
                savedMoviesRepository.insertMovieToWatch(movieUiState.id.toMovieToWatch())
            }
            // Refresh local state status after DB update
            updateStatus(movieUiState.id)
        }
    }

    /**
     * Toggles the "Watched" status in the repository
     */
    fun toggleWatched() {
        viewModelScope.launch {
            if (movieUiState.isWatched) {
                savedMoviesRepository.deleteWatchedMovie(movieUiState.id.toFavouriteMovie())
            } else {
                savedMoviesRepository.insertWatchedMovie(movieUiState.id.toFavouriteMovie())
            }
            // Refresh local state status after DB update
            updateStatus(movieUiState.id)
        }
    }

    /**
     * Internal helper to refresh just the booleans,
     * similar to how updateUiState validates input.
     */
    private suspend fun updateStatus(id: String) {
        val toWatch = savedMoviesRepository.getMovieToWatchById(id).firstOrNull()
        val watched = savedMoviesRepository.getWatchedMovieById(id).firstOrNull()

        movieUiState = movieUiState.copy(
            isFavourite = toWatch != null,
            isWatched = watched != null
        )
    }

    fun deselectMovie() {
        movieUiState = MovieDetailsUiState()
    }
}

