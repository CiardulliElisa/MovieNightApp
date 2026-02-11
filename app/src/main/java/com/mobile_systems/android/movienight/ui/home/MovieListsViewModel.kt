package com.mobile_systems.android.movienight.ui.home

import Movie
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile_systems.android.movienight.data.MoviesRepository
import kotlinx.coroutines.launch

//Represents the three possible states of the movie lists in the home screen

sealed interface MovieUiState {
    data class Success(val categories: Map<String, List<Movie>>) : MovieUiState
    object Error : MovieUiState
    object Loading : MovieUiState
}

//It handles the creation of the movie lists from the API to be shown on the home page
class MovieListsViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {

    var movieUiState: MovieUiState by mutableStateOf(MovieUiState.Loading)
        private set

    //The view model immediately fetches all the movies by category from the API and creates the movie lists
    init {
        fetchAllCategories()
    }

    //Fetches all the movies by category from the API and creates the movie lists
    fun fetchAllCategories() {
        viewModelScope.launch {
            movieUiState = MovieUiState.Loading
            try {

                //define the movie categories
                val genresToFetch = getCategories()
                val resultsMap = mutableMapOf<String, List<Movie>>()

                //for each genre get the list of 10 movies from the API
                for (genre in genresToFetch) {
                    try {
                        val movies: List<Movie> = moviesRepository.getMoviesByGenre(genre, 10)
                        if (movies.isNotEmpty()) {
                            resultsMap[genre] = movies
                        } else {
                            MovieUiState.Error
                        }
                    } catch (e: Exception) {
                        Log.e("MovieDebug", "Error fetching genre $genre", e)
                        movieUiState = MovieUiState.Error
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

    fun getCategories(): List<String> {
        return listOf("Drama", "Animation", "Science Fiction", "Mystery")
    }
}