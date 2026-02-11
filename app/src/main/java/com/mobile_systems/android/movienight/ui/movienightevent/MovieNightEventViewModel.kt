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

    //State for the text input for adding participants
    var friendNameInput by mutableStateOf("")
        private set

    /**Completely resets a movie night event*/
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
            _uiState.update { it.copy(
                isLoadingMovies = true,
                errorMessage = null) }

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
            }  finally {
                _uiState.update { it.copy(isLoadingMovies = false)}
            }
        }
    }

    /** Clears the error message after it has been viewed*/
    fun consumeError() {
        _uiState.update { currentState -> currentState.copy(errorMessage = null) }
    }

    //Keeps track of the new name being input in the text field, only allows names up to 10 letters
    fun updateFriendName(newName: String) {
        if (newName.length <= 10) {
            friendNameInput = newName
        }
    }

    //Opens the dialog window to enter a new participant's name
    fun openEnterNameDialog() {
        _uiState.update { currentState -> currentState.copy(showEnterNameDialog = true) }
    }

    //Closes the dialog window to enter a new participant's name and resets the text field
    fun closeDialog() {
        _uiState.update { currentState -> currentState.copy(showEnterNameDialog = false) }
        friendNameInput = ""
    }

    //Adds user to the movie night event
    fun addFriend() {
        //New participant and assigns a random color and icon to them.
        val newFriend = Friend(
            icon = ICON_POOL.random(),
            color = COLOR_POOL.random(),
            name = friendNameInput
        )
        //Add the new user to the list of participants and close the dialog window
        _uiState.update { it.copy(
            friends = it.friends + newFriend,
            showEnterNameDialog = false
        ) }
        //Reset the text field
        friendNameInput = ""
    }

    //If the icon had already been clicked, it removes the user from the list of participants,
    // otherwise it sets them as the one to be removed (if clicked again)
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

    //The user that was to be removed is no longer to be removed
    fun clearSelection() {
        _uiState.update { it.copy(friendToRemove = null) }
    }

    //Closes the dialog window to add a new participant
    fun closeNewFriendDialog() {
        _uiState.update { currentState -> currentState.copy(showNewFriendDialog = false) }
    }

    //Goes to the next movie in the list of movies to vote on and checks if the round has ended
    fun updateCurrentMovie() {
        val currentState = _uiState.value
        val nextIndex = currentState.currentMovieIndex + 1

        //If there are no more movies to vote on, end the round
        if (nextIndex >= currentState.movieList.size) {
            endMovieNightRound()
            return
        }

        //Go to next movie
        _uiState.update { currentState -> currentState.copy(
            currentMovieIndex = nextIndex,
            currentMovie = currentState.movieList[nextIndex]
        ) }
    }

    //Sort the movies based on their positive votes, negative votes and the movie id
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

    //Update the number of likes for the current movie and goes to the next movie
    fun updateLikes() {
        val movie = _uiState.value.currentMovie ?: return

        // Increment likes for the current movie
        val updatedMovie = movie.copy(likes = movie.likes + 1)

        //Replaces the old movie in the list with the updated movie
        val updatedList = _uiState.value.movieList.map { movieInList ->
            if (movieInList.data.movieId == movie.data.movieId) updatedMovie else movieInList
        }

        _uiState.update { currentState ->
            currentState.copy(
                currentMovie = updatedMovie,
                movieList = updatedList
            )
        }

        updateCurrentMovie()
    }

    //Updates the number of dislikes for the current movie and goes to the next movie
    fun updateDislikes() {
        val movie = _uiState.value.currentMovie ?: return

        val updatedMovie = movie.copy(dislikes = movie.dislikes + 1)

        val updatedList = _uiState.value.movieList.map { movieInList ->
            if (movieInList.data.movieId == movie.data.movieId) updatedMovie else movieInList
        }
        _uiState.update { currentState ->
            currentState.copy(
                currentMovie = updatedMovie,
                movieList = updatedList
            )
        }
        updateCurrentMovie()
    }
}