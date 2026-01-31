package com.mobile_systems.android.movienight.ui

import android.util.Log
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

    var movieDetailsUiState by mutableStateOf(MovieDetailsUiState())
        private set

    fun selectMovie(movieId: String) {
        // 1. Trigger the dialog to show immediately
        movieDetailsUiState = movieDetailsUiState.copy(id = movieId)

        viewModelScope.launch {
            try {
                // 2. Fetch the full MovieDetails object from your repository
                val movieData = moviesRepository.getMovieDetails(movieId)

                val toWatchEntry = savedMoviesRepository.getMovieToWatchById(movieId).firstOrNull()
                val watchedEntry = savedMoviesRepository.getWatchedMovieById(movieId).firstOrNull()

                // 3. MAP THE DATA: This is the missing link!
                movieDetailsUiState = movieDetailsUiState.copy(
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
        val state = movieDetailsUiState
        val movieFromDetails = state.selectedMovie.toMovieToWatch()

// Debug both sources of truth
        Log.d("ID_CHECK", "State Top-Level ID: ${state.id}")
        Log.d("ID_CHECK", "Details Object ID: ${state.selectedMovie.movieId}")
        Log.d("ID_CHECK", "Mapped Entity ID: ${movieFromDetails.id}")
        viewModelScope.launch {
            if (movieDetailsUiState.isToWatch) {
                savedMoviesRepository.deleteMovieToWatch(movieDetailsUiState.selectedMovie.toMovieToWatch())
            } else {
                savedMoviesRepository.insertMovieToWatch(movieDetailsUiState.selectedMovie.toMovieToWatch())
            }
            // Refresh local state status after DB update
            updateStatus(movieDetailsUiState.id)
        }
    }

    /**
     * Toggles the "Watched" status in the repository
     */
    fun toggleWatched() {
        viewModelScope.launch {
            if (movieDetailsUiState.isWatched) {
                savedMoviesRepository.deleteWatchedMovie(movieDetailsUiState.selectedMovie.toWatchedMovie())
            } else {
                savedMoviesRepository.insertWatchedMovie(movieDetailsUiState.selectedMovie.toWatchedMovie())
            }
            // Refresh local state status after DB update
            updateStatus(movieDetailsUiState.id)
        }
    }

    private suspend fun updateStatus(id: String) {
        val toWatch = savedMoviesRepository.getMovieToWatchById(id).firstOrNull()
        val watched = savedMoviesRepository.getWatchedMovieById(id).firstOrNull()

        movieDetailsUiState = movieDetailsUiState.copy(
            isToWatch = toWatch != null,
            isWatched = watched != null
        )
    }

    fun deselectMovie() {
        movieDetailsUiState = MovieDetailsUiState()
    }
}

