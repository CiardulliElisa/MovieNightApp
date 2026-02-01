package com.mobile_systems.android.movienight.ui.home

import Movie

//The ui state for the home page, it simply contains the list of movies to watch
data class HomeUiState(
    val moviesToWatch: List<Movie> = listOf()
)