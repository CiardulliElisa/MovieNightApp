package com.mobile_systems.android.movienight.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mobile_systems.android.movienight.ui.components.ThemeToggleButton

data class Friend(
    val icon: ImageVector,
    val color: Color,
    val name: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddFriendsScreen(
    onStartClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isDarkTheme : Boolean
) {
    // --- STATE ---
    val iconPool = listOf(Icons.Default.Face, Icons.Default.Pets, Icons.Default.Favorite, Icons.Default.AutoAwesome, Icons.Default.Icecream, Icons.Default.RocketLaunch)
    val colorPool = listOf(Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF3F51B5), Color(0xFF00BCD4), Color(0xFF4CAF50), Color(0xFFFF9800))

    val friendsList = remember { mutableStateListOf<Friend>() }
    val friendsToRemove = remember { mutableStateListOf<Friend>() }
    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // We check this to pass it to the Theme Button icon logic
    val isDark = isSystemInDarkTheme()

    // --- ADD FRIEND DIALOG ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enter Friend's Name") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (tempName.isNotBlank()) {
                        friendsList.add(Friend(iconPool.random(), colorPool.random(), tempName))
                        tempName = ""
                        showDialog = false
                    }
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }

    // --- UI LAYOUT ---
    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                friendsToRemove.clear()
            }
    ) {
        // 1. SCROLLABLE CONTENT (Layered first/bottom)
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
                friendsList.forEach { friend ->
                    FriendItem(
                        friend = friend,
                        isPrimed = friendsToRemove.contains(friend),
                        onFriendClick = {
                            if (friendsToRemove.contains(friend)) {
                                friendsList.remove(friend)
                                friendsToRemove.remove(friend)
                            } else {
                                friendsToRemove.clear()
                                friendsToRemove.add(friend)
                            }
                        }
                    )
                }

                // Plus Button
                OutlinedIconButton(
                    onClick = {
                        friendsToRemove.clear()
                        showDialog = true
                    },
                    modifier = Modifier.size(84.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Friend",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        // 2. TOP NAVIGATION ROW (Layered last/top to ensure clickability)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            ThemeToggleButton(
                onThemeToggle = onThemeToggle,
                isDarkTheme = isDarkTheme
            )
        }

        // 3. FIXED START BUTTON
        if (friendsList.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = onStartClicked,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(64.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "START",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun FriendItem(
    friend: Friend,
    isPrimed: Boolean,
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
                containerColor = if (isPrimed) friend.color.copy(alpha = 0.4f) else friend.color
            )
        ) {
            Icon(
                imageVector = if (isPrimed) Icons.Default.Close else friend.icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
        }
        Text(
            text = friend.name,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(top = 8.dp),
            color = if (isPrimed) MaterialTheme.colorScheme.error else Color.Unspecified
        )
    }
}