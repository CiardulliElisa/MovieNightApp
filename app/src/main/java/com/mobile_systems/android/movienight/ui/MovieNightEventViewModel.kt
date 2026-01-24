package com.mobile_systems.android.movienight.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.mobile_systems.android.movienight.data.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private val ICON_POOL = listOf(
    Icons.Default.Face,
    Icons.Default.Pets,
    Icons.Default.Favorite,
    Icons.Default.AutoAwesome,
    Icons.Default.Icecream,
    Icons.Default.RocketLaunch
)
private val COLOR_POOL = listOf(
    Color(0xFFE91E63),
    Color(0xFF9C27B0),
    Color(0xFF3F51B5),
    Color(0xFF00BCD4),
    Color(0xFF4CAF50),
    Color(0xFFFF9800)
)

class MovieNightEventViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MovieNightEventUiState())
    val uiState = _uiState.asStateFlow()

    var friendNameInput by mutableStateOf("")
        private set

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

    fun startMovieNightEvent() {
        val movieList = generateRandomMovieList()
        _uiState.update { it.copy(
            isMovieNightStarted = true,
            movieList = movieList,
            friendsToVote = it.friends
        ) }
        startMovieNightRound()
    }

    fun startMovieNightRound() {
        if (_uiState.value.movieList.isEmpty()) return
        _uiState.update { it.copy(
            currentMovie = it.movieList.firstOrNull(),
            currentMovieIndex = 0,
            currentFriend = it.friendsToVote.randomOrNull(),
            showNewFriendDialog = true
        ) }
    }

    fun closeNewFriendDialog() {
        _uiState.update { it.copy(showNewFriendDialog = false) }
    }

    fun updateCurrentMovie() {
        val currentState = _uiState.value
        val nextIndex = currentState.currentMovieIndex + 1

        if (nextIndex >= currentState.movieList.size) {
            endMovieNightRound()
            return
        }

        _uiState.update { it.copy(
            currentMovieIndex = nextIndex,
            currentMovie = it.movieList[nextIndex]
        ) }
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

    // --- MOVIE DETAILS LOGIC ---
    fun selectMovie(movie: String) {
        _uiState.update { it.copy(
            selectedMovie = movie,
            showMovieDetails = true
        ) }
    }

    fun closeMovieDetails() {
        _uiState.update { currentState -> currentState.copy(
            selectedMovie = null,
            showMovieDetails = false
        ) }
    }

    fun showMovieDetails() {
        _uiState.update { currentState -> currentState.copy(
            selectedMovie = currentState.currentMovie,
            showMovieDetails = true
        )
        }
    }

    private fun generateRandomMovieList(): List<String> {
        return listOf(
            "The Godfather", "Casablanca", "Citizen Kane", "Pulp Fiction",
            "Schindler's List", "The Shawshank Redemption", "Singin' in the Rain",
            "Psycho", "2001: A Space Odyssey", "The Wizard of Oz"
        )
    }
}