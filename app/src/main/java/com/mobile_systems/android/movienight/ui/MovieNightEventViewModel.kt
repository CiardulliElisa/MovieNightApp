package com.mobile_systems.android.movienight.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Icecream
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.mobile_systems.android.movienight.data.Friend
import com.mobile_systems.android.movienight.data.Movie
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

class AddFriendsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MovieNightEventUiState())
    val uiState = _uiState.asStateFlow()

    var friendNameInput by mutableStateOf("")
        private set

    fun updateFriendName(newName: String) {
        friendNameInput = newName
    }

    fun openEnterNameDialog() {
        _uiState.update { currentState -> currentState.copy(showEnterNameDialog = true) }
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
        _uiState.update { currentState -> currentState.copy(
            friends = currentState.friends + newFriend,
            showEnterNameDialog = false
        ) }
        friendNameInput = ""
    }

    //When a friend icon is clicked twice, they are removed
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
        _uiState.update { currentState ->
            // This sets the selection back to nothing
            currentState.copy(friendToRemove = null)
        }
    }

    fun startMovieNightEvent() {
        val movieList = generateRandomMovieList()
        _uiState.update { currentState ->
            currentState.copy(
                isMovieNightStarted = true,
                movieList = movieList
            )
        }
    }

    fun startMovieNightRound() {
        if (_uiState.value.movieList.isEmpty()) return
        _uiState.update { currentState ->
            currentState.copy(
                currentMovie = currentState.movieList.firstOrNull(),
                currentFriend = currentState.friends.randomOrNull(),
                showNewFriendDialog = true
            )
        }
    }

    fun closeNewFriendDialog() {
        _uiState.update { currentState -> currentState.copy(showNewFriendDialog = false) }
    }

    fun endMovieNightRound() {
        _uiState.update { currentState ->
            val remainingFriends = currentState.friends - (currentState.currentFriend !!)

            currentState.copy(
                friendsToVote = remainingFriends,
                isMovieNightFinished = remainingFriends.isEmpty()
            )
        }
    }

    fun generateRandomMovieList() : List<String> {
        return listOf(
            "The Godfather",
            "Casablanca",
            "Citizen Kane",
            "Pulp Fiction",
            "Schindler's List",
            "The Shawshank Redemption",
            "Singin' in the Rain",
            "Psycho",
            "2001: A Space Odyssey",
            "The Wizard of Oz"
        )
    }
}