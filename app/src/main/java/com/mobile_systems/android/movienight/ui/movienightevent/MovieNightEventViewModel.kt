package com.mobile_systems.android.movienight.ui.movienightevent

import Movie
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile_systems.android.movienight.data.Friend
import com.mobile_systems.android.movienight.data.MoviesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val ICON_POOL = listOf(
    Icons.Default.Face,
    Icons.Default.Pets,
    Icons.Default.Favorite,
    Icons.Default.Icecream,
    Icons.Default.RocketLaunch,
    Icons.Default.Person,
    Icons.Default.Eco,
    Icons.Default.Diamond,
    Icons.Default.Cookie,
    Icons.Default.Cake,
    Icons.Default.Bedtime
)
private val COLOR_POOL = listOf(
    Color(0xFFE91E63),
    Color(0xFF9C27B0),
    Color(0xFF3F51B5),
    Color(0xFF00BCD4),
    Color(0xFF4CAF50),
    Color(0xFFFF9800)
)

class MovieNightEventViewModel(
    private val moviesRepository: MoviesRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieNightEventUiState())
    val uiState = _uiState.asStateFlow()

    var friendNameInput by mutableStateOf("")
        private set

    /**This completely reset a movie night event*/
    fun resetMovieNight() {
        _uiState.value = MovieNightEventUiState()
    }

    /** Starts a new movie night event by:
     * - fetching the movies to vote on from the API,
     * - defining who will vote based on the friends gathered elsewhere
     * - defining the first person to vote and the first movie to vote on
     * - showing the start of new round dialog
     */
    fun startMovieNightEvent() {
        viewModelScope.launch {
            _uiState.update { it.copy(errorMessage = null) }

            try {
                val fetchedMovies = moviesRepository.getMovies(10)

                if (fetchedMovies.isEmpty()) {
                    _uiState.update { it.copy(errorMessage = "No movies. Try again!") }
                } else {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isMovieNightStarted = true,
                            movieList = fetchedMovies,
                            friendsToVote = currentState.friends,
                            currentMovie = fetchedMovies.first(),
                            currentMovieIndex = 0,
                            currentFriend = currentState.friends.randomOrNull(),
                            showNewFriendDialog = true,
                        )
                    }
                }
            } catch (e: Exception) {
                // Now this will correctly trigger on airplane mode or API 404s!
                Log.e("VM_ERROR", "Fetch failed", e)
                _uiState.update { it.copy(errorMessage = "Check your internet connection and try again") }
            }
        }
    }

    /**Resets the error message after it has been viewed*/
    fun consumeError() {
        _uiState.update { currentState -> currentState.copy(errorMessage = null) }
    }

    fun updateFriendName(newName: String) {
        friendNameInput = newName
    }

    fun openEnterNameDialog() {
        _uiState.update { it.copy(showEnterNameDialog = true) }
    }

    fun closeDialog() {
        _uiState.update { it.copy(showEnterNameDialog = false) }
        friendNameInput = ""
    }

    fun addFriend() {
        val newFriend = Friend(
            icon = ICON_POOL.random(),
            color = COLOR_POOL.random(),
            name = friendNameInput
        )
        _uiState.update { it.copy(
            friends = it.friends + newFriend,
            showEnterNameDialog = false
        ) }
        friendNameInput = ""
    }

    fun onFriendClicked(friend: Friend) {
        _uiState.update { currentState ->
            if (currentState.friendToRemove == friend) {
                currentState.copy(
                    friends = currentState.friends - friend,
                    friendToRemove = null
                )
            } else {
                currentState.copy(friendToRemove = friend)
            }
        }
    }
    fun clearSelection() {
        _uiState.update { it.copy(friendToRemove = null) }
    }

    fun closeNewFriendDialog() {
        _uiState.update { currentState -> currentState.copy(showNewFriendDialog = false) }
    }

    fun updateCurrentMovie() {
        val currentState = _uiState.value
        val nextIndex = currentState.currentMovieIndex + 1

        if (nextIndex >= currentState.movieList.size) {
            endMovieNightRound()
            return
        }

        _uiState.update { currentState -> currentState.copy(
            currentMovieIndex = nextIndex,
            currentMovie = currentState.movieList[nextIndex]
        ) }
    }

    fun getSortedRankingList(): List<Movie> {
        return _uiState.value.movieList.sortedWith(
            compareByDescending<Movie> { it.likes }
                .thenBy { it.dislikes }
                .thenBy { it.data.movieId }
        )
    }

    fun endMovieNightRound() {
        _uiState.update { currentState ->
            val remainingFriends = currentState.friendsToVote - (currentState.currentFriend!!)
            val isFinished = remainingFriends.isEmpty()

            if (isFinished) {
                currentState.copy(
                    friendsToVote = emptyList(),
                    isMovieNightFinished = true
                )
            } else {
                val nextFriend = remainingFriends.random()
                currentState.copy(
                    friendsToVote = remainingFriends,
                    currentFriend = nextFriend,
                    currentMovieIndex = 0,
                    currentMovie = currentState.movieList[0],
                    showNewFriendDialog = true
                )
            }
        }
    }

    fun updateLikes() {
        val movie = _uiState.value.currentMovie ?: return

        // 1. Create a new movie object with incremented likes
        val updatedMovie = movie.copy(likes = movie.likes + 1)

        // 2. Update the list to include this updated movie (so the final summary is correct)
        val updatedList = _uiState.value.movieList.map { movieInList ->
            if (movieInList.data.movieId == movie.data.movieId) updatedMovie else movieInList
        }

        // 3. Emit a whole new UI State
        _uiState.update { currentState ->
            currentState.copy(
                currentMovie = updatedMovie,
                movieList = updatedList
            )
        }

        updateCurrentMovie()
    }

    fun updateDislikes() {
        val movie = _uiState.value.currentMovie ?: return

        // 1. Create a new movie object with incremented likes
        val updatedMovie = movie.copy(dislikes = movie.dislikes + 1)

        // 2. Update the list to include this updated movie (so the final summary is correct)
        val updatedList = _uiState.value.movieList.map { movieInList ->
            if (movieInList.data.movieId == movie.data.movieId) updatedMovie else movieInList
        }

        // 3. Emit a whole new UI State
        _uiState.update { currentState ->
            currentState.copy(
                currentMovie = updatedMovie,
                movieList = updatedList
            )
        }

        updateCurrentMovie()
    }
}