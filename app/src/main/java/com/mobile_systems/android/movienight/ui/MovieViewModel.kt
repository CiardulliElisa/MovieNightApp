package com.mobile_systems.android.movienight.ui

import Movie
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile_systems.android.movienight.data.MoviesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

sealed interface MovieUiState {
    data class Success(val categories: Map<String, List<Movie>>) : MovieUiState
    object Error : MovieUiState
    object Loading : MovieUiState
}

class MovieViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {

    var movieUiState: MovieUiState by mutableStateOf(MovieUiState.Loading)
        private set

    init {
        fetchAllCategories()
    }

    private fun fetchAllCategories() {
        viewModelScope.launch {
            movieUiState = MovieUiState.Loading
            try {
                val genresToFetch = listOf("Drama", "Animation", "Science Fiction", "Mystery")

                val resultsMap = mutableMapOf<String, List<Movie>>()

                for (genre in genresToFetch) {
                    try {
                        val movies: List<Movie> = moviesRepository.getMoviesByGenre(genre, 10)

                        Log.d("MovieDebug", "Genre: $genre | Items found: ${movies.size}")

                        if (movies.isNotEmpty()) {
                            resultsMap[genre] = movies
                        }
                    } catch (e: Exception) {
                        Log.e("MovieDebug", "Error fetching genre $genre", e)
                        // We don't stop the whole app, just skip this specific genre
                    }
                }

                movieUiState = if (resultsMap.isNotEmpty()) {
                    MovieUiState.Success(resultsMap)
                } else {
                    MovieUiState.Error
                }

            } catch (e: Exception) {
                Log.e("MovieDebug", "Critical failure", e)
                movieUiState = MovieUiState.Error
            }
        }
    }
}