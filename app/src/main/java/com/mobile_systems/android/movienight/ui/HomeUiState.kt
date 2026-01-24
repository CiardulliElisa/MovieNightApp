package com.mobile_systems.android.movienight.ui

//The UI State is a simple data class that represents everything the user sees on the screen. If it’s not in the state, it shouldn't be on the screen.
//
//What to put in it:
//
//Business Data: Lists of items (e.g., movies: List<Movie>), user details, or scores.
//
//UI Status: Booleans like isLoading, isRefreshing, or isError.
//
//User Input (Temporary): The current text in a search bar or whether a checkbox is ticked.
//
//Navigation Events: Sometimes used to trigger a "Go to Details" action.

data class HomeUiState(

    val movieCategories: List<String> = listOf("Watchlist", "Action", "Comedies", "Drama", "Horror"),

)