package com.mobile_systems.android.movienight.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ThemeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ThemeUiState())
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    //Toggle from light to dark theme, or from dark to light theme
    fun toggleDarkTheme() {
        _uiState.update { currentState ->
            currentState.copy(
                isDarkTheme = !currentState.isDarkTheme
            )
        }
    }
}

