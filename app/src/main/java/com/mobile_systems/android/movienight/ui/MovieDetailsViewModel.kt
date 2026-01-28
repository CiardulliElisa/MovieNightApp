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

    var movieUiState by mutableStateOf(MovieDetailsUiState())
        private set

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

