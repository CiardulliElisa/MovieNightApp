package com.mobile_systems.android.movienight.ui

import com.mobile_systems.android.movienight.data.Friend
import com.mobile_systems.android.movienight.data.Movie

data class MovieNightEventUiState(

    //Adding and removing friends to movie night event
    val friends: List<Friend> = emptyList(),
    val friendToRemove: Friend? = null,
    val showEnterNameDialog: Boolean = false,

    //Handling movie night event
    var isMovieNightStarted: Boolean = false,
    val movieList: List<Movie> = emptyList(),
    var currentMovie: Movie? = null,
    var currentFriend: Friend? = null,
    var showNewFriendDialog: Boolean = false,
    var isMovieNightFinished: Boolean = false,
)
