package com.mobile_systems.android.movienight.ui.movienightevent

import Movie
import com.mobile_systems.android.movienight.data.Friend

data class MovieNightEventUiState(

    //Handling the loading of the movies
    val isLoadingMovies: Boolean = false,

    //Adding and removing friends to participate in the movie night event
    val friends: List<Friend> = emptyList(),
    val friendToRemove: Friend? = null,
    val showEnterNameDialog: Boolean = false,

    //Tracking the current movie and whose turn it is to vote
    val currentMovie: Movie? = null,
    val currentFriend: Friend? = null,
    val movieList: List<Movie> = emptyList(),
    val showNewFriendDialog: Boolean = false,
    val currentMovieIndex : Int = 0,
    val friendsToVote: List<Friend> = friends,

    //Handling movie night event start and end
    val isMovieNightStarted: Boolean = false,
    val isMovieNightFinished: Boolean = false,

    //Handles errors in the movie night event
    val errorMessage: String? = null
)
