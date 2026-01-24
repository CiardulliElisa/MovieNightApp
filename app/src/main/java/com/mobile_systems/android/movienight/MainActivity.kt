package com.mobile_systems.android.movienight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mobile_systems.android.movienight.ui.theme.MovieNightTheme

//If it’s about how things look (colors, padding, animations), put it in the UI/Composable.
//
//If it’s about what data is shown (names, dates, loading spinners), put it in the UI State.
//
//If it’s about getting or changing that data, put it in the ViewModel.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MovieNightApp()
        }
    }
}
