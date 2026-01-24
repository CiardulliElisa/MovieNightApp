package com.mobile_systems.android.movienight.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mobile_systems.android.movienight.data.Friend

@Composable
fun FriendIcon(
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
                containerColor = friend.color
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
            color = Color.White
        )
    }
}