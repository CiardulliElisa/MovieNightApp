package com.mobile_systems.android.movienight.ui.movienightevent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.mobile_systems.android.movienight.data.Friend
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton
import com.mobile_systems.android.movienight.ui.home.LoadingScreen

/** This screen is used to add participants to the movie night event and to actually start the voting process */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddFriendsScreen(
    onNavigateToVote: () -> Unit,
    movieNightEventViewModel: MovieNightEventViewModel,
    themeViewModel : ThemeViewModel,
    onStartClicked: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Ui states
    val movieNightEventUiState by movieNightEventViewModel.uiState.collectAsState()
    val themeUiState by themeViewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val friendNameInput = movieNightEventViewModel.friendNameInput

    // Only navigate to the voting page if the movie night has actually started
    LaunchedEffect(movieNightEventUiState.isMovieNightStarted) {
        if (movieNightEventUiState.isMovieNightStarted) {
            onNavigateToVote()
        }
    }

    //If the movie night cannot start due to an error show the user an error message
    LaunchedEffect(movieNightEventUiState.errorMessage) {
        if(movieNightEventUiState.errorMessage != null) {
            snackbarHostState.showSnackbar(
                message = movieNightEventUiState.errorMessage!!,
                duration = SnackbarDuration.Short
            )
            movieNightEventViewModel.consumeError()
        }
    }

    // Dialog that shows up to enter a user's name
    if (movieNightEventUiState.showEnterNameDialog) {
        AddFriendDialog(
            friendNameInput = friendNameInput,
            onNameChange = { movieNightEventViewModel.updateFriendName(it) },
            onConfirm = { movieNightEventViewModel.addFriend() },
            onDismiss = { movieNightEventViewModel.closeDialog() }
        )
    }

    //When a user clicks anywhere on the screen, the selection of the currently selected icon is cleared
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                movieNightEventViewModel.clearSelection()
            }
    ) {
        if (movieNightEventUiState.isLoadingMovies) {
            MovieNightLoadingScreen(
                message = "Loading movies...",
                modifier = Modifier.fillMaxSize()
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 120.dp)
        ) {
            //Title of the page
            Text(
                text = "Add Friends",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Row of icons representing the participants
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                maxItemsInEachRow = 3
            ) {
                //Create an icon for each user participating in the movie night
                movieNightEventUiState.friends.forEach { friend ->
                    FriendIcon(
                        friend = friend,
                        isSelected = movieNightEventUiState.friendToRemove == friend,
                        onFriendClick = { movieNightEventViewModel.onFriendClicked(friend) }
                    )
                }

                //Plus icon button to add a new user to the movie night
                OutlinedIconButton(
                    onClick = { movieNightEventViewModel.openEnterNameDialog() },
                    modifier = Modifier.size(84.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Friend"
                    )
                }
            }
        }

        // Top row, containing the back arrow to go back to the home screen and the theme toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Back arrow
            IconButton(onClick = onBackClicked) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }

            //Theme toggle button
            ThemeToggleButton(
                onThemeToggle = { themeViewModel.toggleDarkTheme() },
                isDarkTheme = themeUiState.isDarkTheme,
            )
        }

        //A possible error will appear above the start button
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )

        // Start Button to start the voting process, can only be started if there is at least one participant
        if (movieNightEventUiState.friends.isNotEmpty()) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = { onStartClicked() },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(64.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Text("START", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

//Dialog box to enter the name of a new participant to the movie night event
@Composable
fun AddFriendDialog(
    friendNameInput: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Friend") },
        text = {
            OutlinedTextField(
                value = friendNameInput,
                onValueChange = onNameChange,
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// A colored icon representing a participant in the movie night.
// It can be pressed to be selected and pressed again to be deleted
@Composable
fun FriendIcon(
    friend: Friend,
    isSelected: Boolean,
    onFriendClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        FilledTonalIconButton(
            onClick = onFriendClick,
            modifier = Modifier.size(84.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = friend.color
            )
        ) {
            Icon(
                imageVector = if (isSelected) Icons.Default.Close else friend.icon,
                contentDescription = "Random Friend Icon",
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
        }

        //Label underneath the icon, containing the name of the user
        Text(
            text = friend.name,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp),
            color = Color.White
        )
    }
}

//The loading screen for when the movies are being fetched for the movie night event
@Composable
fun MovieNightLoadingScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}