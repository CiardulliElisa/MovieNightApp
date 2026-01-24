package com.mobile_systems.android.movienight.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//The ViewModel is a class that survives configuration changes (like rotating the screen). Its job is to manage the UI State and handle the "brain work."
//
//What to put in it:
//
//State Management: Holding that MutableStateFlow we discussed earlier.
//
//Data Fetching: Calling your API or Database (usually via a Repository).
//
//Event Handling: Functions that react to user actions (e.g., onMovieClicked(movie: Movie) or onSearchQueryChanged(query: String)).
//
//Logic: Filtering lists or validating if a "Submit" button should be enabled.

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}