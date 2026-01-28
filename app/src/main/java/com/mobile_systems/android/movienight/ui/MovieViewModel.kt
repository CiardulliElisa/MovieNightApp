package com.mobile_systems.android.movienight.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mobile_systems.android.movienight.MovieNightApplication
import com.mobile_systems.android.movienight.model.Movie
import com.mobile_systems.android.movienight.data.MoviesRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface MovieUiState {
    data class Success(val movies: List<Movie>) : MovieUiState
    object Error : MovieUiState
    object Loading : MovieUiState
}

class MovieViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {

    var movieUiState: MovieUiState by mutableStateOf(MovieUiState.Loading)
        private set

    init {
        getRandomMovies(20)
    }

    fun getRandomMovies(count: Int = 20) {
        viewModelScope.launch {
            movieUiState = MovieUiState.Loading

            movieUiState = try {
                // 1. Create the pool of candidate IDs (casting a wide net)
                val idPool = (1000000..1500000).shuffled().take(count * 3).map { "tt$it" }
                val validMovies = mutableListOf<Movie>()

                // 2. Loop until we find enough valid movies
                for (id in idPool) {
                    if (validMovies.size >= count) break

                    try {
                        // Ask repository for a single movie
                        val movie = moviesRepository.getMovie(id)

                        // 3. Only add if it has a name and an image
                        if (movie.info.name.isNotBlank() && movie.info.image.isNotBlank()) {
                            validMovies.add(movie)
                        }
                    } catch (e: Exception) {
                        // Skip 404s or bad IDs
                        continue
                    }
                }

                // 4. Update the UI state with our list
                if (validMovies.isNotEmpty()) {
                    MovieUiState.Success(validMovies)
                } else {
                    MovieUiState.Error
                }

            } catch (e: IOException) {
                MovieUiState.Error
            } catch (e: HttpException) {
                MovieUiState.Error
            } catch (e: Exception) {
                MovieUiState.Error
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as MovieNightApplication)
                val moviesRepository = application.container.moviesRepository
                MovieViewModel(moviesRepository = moviesRepository)
            }
        }
    }
}