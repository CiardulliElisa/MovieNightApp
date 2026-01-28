package com.mobile_systems.android.movienight.ui.movienightevent

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobile_systems.android.movienight.data.Friend
import com.mobile_systems.android.movienight.data.MoviesRepository
import com.mobile_systems.android.movienight.model.Movie
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
        viewModelScope.launch {
            // Start by setting a loading state if you have one,
            // or just proceed with the fetch
            try {
                // 1. Logic to fetch random movies directly using the repository
                val idPool = (1000000..1500000).take(60).map { "tt$it" }
                val fetchedMovies = mutableListOf<Movie>()

                for (id in idPool) {
                    if (fetchedMovies.size >= 20) break
                    try {
                        val movie = moviesRepository.getMovie(id)
                        if (movie.info.name.isNotBlank() && movie.info.image.isNotBlank()) {
                            fetchedMovies.add(movie)
                        }
                    } catch (e: Exception) {
                        continue // Skip failed IDs
                    }
                }

                // 2. Only start the event if we actually found movies
                if (fetchedMovies.isNotEmpty()) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isMovieNightStarted = true,
                            movieList = fetchedMovies,
                            friendsToVote = currentState.friends,
                            currentMovie = fetchedMovies.firstOrNull(),
                            currentMovieIndex = 0,
                            currentFriend = currentState.friends.randomOrNull(),
                            showNewFriendDialog = true
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun startMovieNightRound() {
        if (_uiState.value.movieList.isEmpty()) return
        _uiState.update { currentState -> currentState.copy(
            currentMovie = currentState.movieList.firstOrNull(),
            currentMovieIndex = 0,
            currentFriend = currentState.friendsToVote.randomOrNull(),
            showNewFriendDialog = true
        ) }
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

    /**
     * Takes the current movie list and returns a version sorted by popularity.
     * Sorting logic:
     * 1. Most Likes (Primary)
     * 2. Least Dislikes (Secondary tie-breaker)
     */
    fun getSortedRankingList(): List<Movie> {
        return _uiState.value.movieList.sortedWith(
            compareByDescending<Movie> { it.likes }
                .thenBy { it.dislikes }
                .thenBy { it.info.name } // Final tie-breaker: alphabetical order
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

    // --- MOVIE DETAILS LOGIC ---
    fun selectMovie(movie: Movie) {
        _uiState.update { currentState -> currentState.copy(
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

    fun updateLikes() {
        val movie = _uiState.value.currentMovie ?: return

        // 1. Create a new movie object with incremented likes
        val updatedMovie = movie.copy(likes = movie.likes + 1)

        // 2. Update the list to include this updated movie (so the final summary is correct)
        val updatedList = _uiState.value.movieList.map { movieInList ->
            if (movieInList.id == movie.id) updatedMovie else movieInList
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

    fun resetMovieNight() {
        _uiState.value = MovieNightEventUiState()
    }

    fun updateDislikes() {
        val movie = _uiState.value.currentMovie ?: return

        // 1. Create a new movie object with incremented likes
        val updatedMovie = movie.copy(dislikes = movie.dislikes + 1)

        // 2. Update the list to include this updated movie (so the final summary is correct)
        val updatedList = _uiState.value.movieList.map { movieInList ->
            if (movieInList.id == movie.id) updatedMovie else movieInList
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