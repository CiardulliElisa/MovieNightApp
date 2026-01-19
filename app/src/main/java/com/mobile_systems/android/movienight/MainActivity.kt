package com.mobile_systems.android.movienight

import com.mobile_systems.android.movienight.ui.VoteScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.mobile_systems.android.movienight.ui.theme.MovieNightTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MovieNightTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {innerPadding ->
                    VoteScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
