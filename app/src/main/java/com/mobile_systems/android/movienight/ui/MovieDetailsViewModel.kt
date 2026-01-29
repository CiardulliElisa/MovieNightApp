package com.mobile_systems.android.movienight.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile_systems.android.movienight.data.MoviesRepository
import com.mobile_systems.android.movienight.data.SavedMoviesRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class MovieDetailsViewModel(
    private val savedMoviesRepository: SavedMoviesRepository,
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    var movieUiState by mutableStateOf(MovieDetailsUiState())
        private set

    fun selectMovie(movieId: String) {
        viewModelScope.launch {
            try {
                // 1. Fetch the FULL response object, not just the title string
                val title = moviesRepository.getMovieTitle(movieId)

                val toWatchEntry = savedMoviesRepository.getMovieToWatchById(movieId).firstOrNull()
                val watchedEntry = savedMoviesRepository.getWatchedMovieById(movieId).firstOrNull()

                // 2. Map the API data to your UI State
                movieUiState = MovieDetailsUiState(
                    id = movieId,
                    title = title,
                    isToWatch = toWatchEntry != null,
                    isWatched = watchedEntry != null
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Toggles the "To Watch" status in the repository
     */
    fun toggleToWatch() {
        viewModelScope.launch {
            if (movieUiState.isToWatch) {
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
            isToWatch = toWatch != null,
            isWatched = watched != null
        )
    }

    fun deselectMovie() {
        movieUiState = MovieDetailsUiState()
    }
}

