package com.mobile_systems.android.movienight.ui

data class AddFriendsUiState(
    val friends: List<Friend> = emptyList(),
    val friendToRemove: Friend? = null,
    val showEnterNameDialog: Boolean = false,
)