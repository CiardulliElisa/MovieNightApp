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
        // 1. Trigger the dialog to show immediately
        movieUiState = movieUiState.copy(id = movieId)

        viewModelScope.launch {
            try {
                // 2. Fetch the full MovieDetails object from your repository
                val movieData = moviesRepository.getMovieDetails(movieId)
                println("DEBUG: API Response Title: ${movieData.title}")
                println("DEBUG: API Response Trailer: ${movieData.trailer}")

                val toWatchEntry = savedMoviesRepository.getMovieToWatchById(movieId).firstOrNull()
                val watchedEntry = savedMoviesRepository.getWatchedMovieById(movieId).firstOrNull()

                // 3. MAP THE DATA: This is the missing link!
                movieUiState = movieUiState.copy(
                    selectedMovie = movieData,
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
                savedMoviesRepository.deleteMovieToWatch(movieUiState.selectedMovie.toMovieToWatch())
            } else {
                savedMoviesRepository.insertMovieToWatch(movieUiState.selectedMovie.toMovieToWatch())
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
                savedMoviesRepository.deleteWatchedMovie(movieUiState.selectedMovie.toWatchedMovie())
            } else {
                savedMoviesRepository.insertWatchedMovie(movieUiState.selectedMovie.toWatchedMovie())
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

