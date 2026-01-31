package com.mobile_systems.android.movienight.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile_systems.android.movienight.data.SavedMoviesRepository
import com.mobile_systems.android.movienight.ui.toMovie
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    createSavedStateHandle: SavedStateHandle,
    savedMoviesRepository: SavedMoviesRepository,
) : ViewModel() {

    /**
     * The UI state is derived directly from the repository flow.
     * This eliminates the need for a manual MutableStateFlow (_uiState).
     */
    val homeUiState: StateFlow<HomeUiState> =
        savedMoviesRepository.getAllMoviesToWatch()
            .map { list ->
                val parsedMovies = list.map { item ->
                    item.toMovie()
                }
                HomeUiState(moviesToWatch = parsedMovies)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = HomeUiState()
            )
}