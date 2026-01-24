package com.mobile_systems.android.movienight

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobile_systems.android.movienight.ui.AddFriendsScreen
import com.mobile_systems.android.movienight.ui.HomeScreen
import com.mobile_systems.android.movienight.ui.HomeViewModel
import com.mobile_systems.android.movienight.ui.MovieNightEventViewModel
import com.mobile_systems.android.movienight.ui.RankingListScreen
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.VoteScreen
import com.mobile_systems.android.movienight.ui.theme.MovieNightTheme

enum class MovieNightApp() {
    Home(),
    AddFriends(),
    Vote(),
    RankingList(),
}

@Composable
fun MovieNightApp(
    navController: NavHostController = rememberNavController()
) {
    val themeViewModel: ThemeViewModel = viewModel()
    val movieNightEventViewModel: MovieNightEventViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()
    var isDarkTheme by remember { mutableStateOf(true) }

    MovieNightTheme(darkTheme = isDarkTheme) {
        Scaffold(
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = MovieNightApp.Home.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = MovieNightApp.Home.name) {
                    HomeScreen(
                        onMovieNightClicked = { navController.navigate(MovieNightApp.AddFriends.name) },
                        homeViewModel = homeViewModel,
                        modifier = Modifier,
                        themeViewModel = themeViewModel
                    )
                }
                composable(route = MovieNightApp.AddFriends.name) {
                    AddFriendsScreen(
                        onStartClicked = { navController.navigate(MovieNightApp.Vote.name) },
                        onBackClicked = {navController.popBackStack()},
                        movieNightEventViewModel = movieNightEventViewModel,
                        themeViewModel = themeViewModel,
                        modifier = Modifier
                    )
                }
                composable(route = MovieNightApp.Vote.name) {
                    VoteScreen(
                        movieNightEventViewModel = movieNightEventViewModel,
                        onMovieNightFinished = { navController.navigate(MovieNightApp.RankingList.name) },
                        modifier = Modifier
                    )
                }
                composable(route = MovieNightApp.RankingList.name) {
                    RankingListScreen(
                        movieNightEventViewModel = movieNightEventViewModel,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}