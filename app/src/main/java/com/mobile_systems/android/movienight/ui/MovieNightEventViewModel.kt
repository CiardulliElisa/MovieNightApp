package com.mobile_systems.android.movienight.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.currentComposer
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
                .thenBy { it.title } // Final tie-breaker: alphabetical order
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

    private fun generateRandomMovieList(): List<Movie> {
        return listOf(
            Movie(
                id = "1",
                title = "The Godfather",
                description = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
                rating = 9.2,
                posterUrl = "godfather_poster", // Placeholder for local drawable or URL
                genre = listOf("Crime", "Drama"),
                actors = listOf("Marlon Brando", "Al Pacino", "James Caan"),
                runtime = "2h 55m",
                director = "Francis Ford Coppola",
                releaseDate = "1972",
                likes = 0,
                dislikes = 0
            ),
            Movie(
                id = "2",
                title = "Pulp Fiction",
                description = "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.",
                rating = 8.9,
                posterUrl = "pulp_fiction_poster",
                genre = listOf("Crime", "Drama"),
                actors = listOf("John Travolta", "Uma Thurman", "Samuel L. Jackson"),
                runtime = "2h 34m",
                director = "Quentin Tarantino",
                releaseDate = "1994",
                likes = 0,
                dislikes = 0
            ),
            Movie(
                id = "3",
                title = "The Shawshank Redemption",
                description = "Over the course of several years, two convicts form a friendship, seeking consolation and, eventually, redemption through basic compassion.",
                rating = 9.3,
                posterUrl = "shawshank_poster",
                genre = listOf("Drama"),
                actors = listOf("Tim Robbins", "Morgan Freeman", "Bob Gunton"),
                runtime = "2h 22m",
                director = "Frank Darabont",
                releaseDate = "1994",
                likes = 0,
                dislikes = 0
            ),
            Movie(
                id = "4",
                title = "2001: A Space Odyssey",
                description = "After uncovering a mysterious artifact buried beneath the Lunar surface, mankind sets off on a quest to find its origins with the help of intelligent supercomputer H.A.L. 9000.",
                rating = 8.3,
                posterUrl = "space_odyssey_poster",
                genre = listOf("Sci-Fi", "Adventure"),
                actors = listOf("Keir Dullea", "Gary Lockwood", "William Sylvester"),
                runtime = "2h 29m",
                director = "Stanley Kubrick",
                releaseDate = "1968",
                likes = 0,
                dislikes = 0
            ),
            Movie(
                id = "5",
                title = "The Wizard of Oz",
                description = "Dorothy Gale is swept away from a farm in Kansas to a magical land of Oz in a tornado and embarks on a quest with her new friends to see the Wizard.",
                rating = 8.1,
                posterUrl = "wizard_oz_poster",
                genre = listOf("Adventure", "Family", "Fantasy"),
                actors = listOf("Judy Garland", "Frank Morgan", "Ray Bolger"),
                runtime = "1h 42m",
                director = "Victor Fleming",
                releaseDate = "1939",
                likes = 0,
                dislikes = 0
            )
        ).shuffled().take(5)
    }
}