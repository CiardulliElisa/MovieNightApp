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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.components.FriendIcon
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton

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

    // Dialog that shows up to enter a name
    if (movieNightEventUiState.showEnterNameDialog) {
        AddFriendDialog(
            friendNameInput = friendNameInput,
            onNameChange = { movieNightEventViewModel.updateFriendName(it) },
            onConfirm = { movieNightEventViewModel.addFriend() },
            onDismiss = { movieNightEventViewModel.closeDialog() }
        )
    }

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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(top = 100.dp, start = 16.dp, end = 16.dp, bottom = 120.dp)
        ) {
            Text(
                text = "Add Friends",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                maxItemsInEachRow = 3
            ) {
                // Use list from uiState
                movieNightEventUiState.friends.forEach { friend ->
                    FriendIcon(
                        friend = friend,
                        isPrimed = movieNightEventUiState.friendToRemove == friend,
                        onFriendClick = { movieNightEventViewModel.onFriendClicked(friend) }
                    )
                }

                OutlinedIconButton(
                    onClick = { movieNightEventViewModel.openEnterNameDialog() },
                    modifier = Modifier.size(84.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Friend")
                }
            }
        }

        // TOP NAVIGATION ROW
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
            }

            ThemeToggleButton(
                onThemeToggle = { themeViewModel.toggleDarkTheme() },
                isDarkTheme = themeUiState.isDarkTheme,
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
                .padding(bottom = 90.dp)
        )

        // START BUTTON
        if (movieNightEventUiState.friends.isNotEmpty()) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = {
                        movieNightEventViewModel.startMovieNightEvent()
                        onStartClicked()

                              },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(64.dp),
                    shape = RoundedCornerShape(12.dp)
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