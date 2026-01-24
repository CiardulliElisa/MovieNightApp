package com.mobile_systems.android.movienight

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
        Surface(
        ) { NavHost(
                navController = navController,
                startDestination = MovieNightApp.Home.name,
                modifier = Modifier.safeDrawingPadding()
            ) {
                composable(route = MovieNightApp.Home.name) {
                    HomeScreen(
                        onMovieNightClicked = {
                            navController.navigate(MovieNightApp.AddFriends.name)
                            movieNightEventViewModel.resetMovieNight() },
                        homeViewModel = homeViewModel,
                        modifier = Modifier,
                        themeViewModel = themeViewModel
                    )
                }
                composable(route = MovieNightApp.AddFriends.name) {
                    AddFriendsScreen(
                        onStartClicked = {
                            navController.navigate(MovieNightApp.Vote.name) {
                                popUpTo(MovieNightApp.AddFriends.name) { inclusive = true }
                            }
                        },
                        onBackClicked = {navController.popBackStack()},
                        movieNightEventViewModel = movieNightEventViewModel,
                        themeViewModel = themeViewModel,
                        modifier = Modifier
                    )
                }
                composable(route = MovieNightApp.Vote.name) {
                    VoteScreen(
                        movieNightEventViewModel = movieNightEventViewModel,
                        onMovieNightFinished = {
                            navController.navigate(MovieNightApp.RankingList.name) {
                                popUpTo(MovieNightApp.Home.name) { inclusive = false }
                            }
                        },
                        onHomeClicked = { navController.navigate(MovieNightApp.Home.name) },
                        onTryAgainClicked = { navController.navigate(MovieNightApp.AddFriends.name) },
                        modifier = Modifier
                    )
                }
                composable(route = MovieNightApp.RankingList.name) {
                    RankingListScreen(
                        movieNightEventViewModel = movieNightEventViewModel,
                        onHomeClicked = { navController.navigate(MovieNightApp.Home.name) },
                        onTryAgainClicked = { navController.navigate(MovieNightApp.AddFriends.name) },
                        modifier = Modifier
                    )
                }
            }
        }
    }
}