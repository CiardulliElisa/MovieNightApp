package com.mobile_systems.android.movienight.ui

import com.mobile_systems.android.movienight.data.Friend
import com.mobile_systems.android.movienight.data.Movie

data class MovieNightEventUiState(

    //Adding and removing friends to movie night event
    val friends: List<Friend> = emptyList(),
    val friendToRemove: Friend? = null,
    val showEnterNameDialog: Boolean = false,

    //Handling movie night event
    val isMovieNightStarted: Boolean = false,
    val movieList: List<Movie> = emptyList(),
    val currentMovie: Movie? = null,
    val currentFriend: Friend? = null,
    val showNewFriendDialog: Boolean = false,
    val isMovieNightFinished: Boolean = false,
    val friendsToVote: List<Friend> = friends,
    val currentMovieIndex : Int = 0,
    val showMovieDetails: Boolean = false,
    val selectedMovie: Movie? = null,
)
