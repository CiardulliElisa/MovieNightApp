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

//Manages the logic behind viewing the movie details of a selected movie
//It handles the display of data from the API and from the local database.
class MovieDetailsViewModel(
    private val savedMoviesRepository: SavedMoviesRepository,
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    var movieDetailsUiState by mutableStateOf(MovieDetailsUiState())
        private set

    //Updates the ui state with the information from the API about the selected movie
    // and the watched or to watch information about the movie from the local database
    fun selectMovie(movieId: String) {

        movieDetailsUiState = movieDetailsUiState.copy(isSelected = true, isLoading = true)

        viewModelScope.launch {
            try {
                //Get the movie details from the API
                val movieData = moviesRepository.getMovieDetails(movieId)

                //Get the watched or to watch information from the local database
                val toWatchEntry = savedMoviesRepository.getMovieToWatchById(movieId).firstOrNull()
                val watchedEntry = savedMoviesRepository.getWatchedMovieById(movieId).firstOrNull()

                //Update the ui state with the new information about the selected movie
                movieDetailsUiState = movieDetailsUiState.copy(
                    selectedMovie = movieData,
                    isToWatch = toWatchEntry != null,
                    isWatched = watchedEntry != null,
                    isLoading = false
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Adds or removes the selected movie from the to watch database
    fun toggleToWatch() {
        viewModelScope.launch {
            if (movieDetailsUiState.isToWatch) {
                savedMoviesRepository.deleteMovieToWatch(movieDetailsUiState.selectedMovie.toMovieToWatch())
            } else {
                savedMoviesRepository.insertMovieToWatch(movieDetailsUiState.selectedMovie.toMovieToWatch())
            }
            // Refresh local state status after DB update
            updateStatus(movieDetailsUiState.selectedMovie.movieId)
        }
    }

    //Adds or removes the selected movie from the watched database
    fun toggleWatched() {
        viewModelScope.launch {
            if (movieDetailsUiState.isWatched) {
                savedMoviesRepository.deleteWatchedMovie(movieDetailsUiState.selectedMovie.toWatchedMovie())
            } else {
                savedMoviesRepository.insertWatchedMovie(movieDetailsUiState.selectedMovie.toWatchedMovie())
            }
            // Refresh local state status after DB update
            updateStatus(movieDetailsUiState.selectedMovie.movieId)
        }
    }

    //Makes sure the ui status remains updated with the local database information
    private suspend fun updateStatus(id: String) {
        val toWatch = savedMoviesRepository.getMovieToWatchById(id).firstOrNull()
        val watched = savedMoviesRepository.getWatchedMovieById(id).firstOrNull()

        movieDetailsUiState = movieDetailsUiState.copy(
            isToWatch = toWatch != null,
            isWatched = watched != null
        )
    }

    //Deselects the selected movie and resets the ui state
    fun deselectMovie() {
        movieDetailsUiState = movieDetailsUiState.copy(isSelected = false)
        movieDetailsUiState = MovieDetailsUiState()
    }
}

