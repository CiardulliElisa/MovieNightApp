package com.mobile_systems.android.movienight.ui

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
import com.mobile_systems.android.movienight.ui.components.FriendIcon
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddFriendsScreen(
    addFriendsViewModel: AddFriendsViewModel,
    themeViewModel : ThemeViewModel,
    onStartClicked: () -> Unit,
    onBackClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val addFriendsUiState by addFriendsViewModel.uiState.collectAsState()
    val movieNightUiState by themeViewModel.uiState.collectAsState()

    val friendNameInput = addFriendsViewModel.friendNameInput

    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }

    // --- ADD FRIEND DIALOG ---
    if (addFriendsUiState.showEnterNameDialog) {
        AlertDialog(
            onDismissRequest = { addFriendsViewModel.closeDialog() },
            title = { Text("Add Friend") },
            text = {
                OutlinedTextField(
                    // Use VM state
                    value = friendNameInput,
                    onValueChange = { addFriendsViewModel.updateFriendName(it) },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            },
            confirmButton = {
                TextButton(onClick = { addFriendsViewModel.addFriend() }) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { addFriendsViewModel.closeDialog() }) {
                    Text("Cancel")
                }
            }
        )
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                addFriendsViewModel.clearSelection()
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
                addFriendsUiState.friends.forEach { friend ->
                    FriendIcon(
                        friend = friend,
                        // Check single selection in VM
                        isPrimed = addFriendsUiState.friendToRemove == friend,
                        onFriendClick = { addFriendsViewModel.onFriendClicked(friend) }
                    )
                }

                OutlinedIconButton(
                    onClick = { addFriendsViewModel.openEnterNameDialog() },
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
                isDarkTheme = movieNightUiState.isDarkTheme
            )
        }

        // START BUTTON
        if (addFriendsUiState.friends.isNotEmpty()) {
            Surface(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = onStartClicked,
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(64.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("START", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}