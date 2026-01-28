package com.mobile_systems.android.movienight

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mobile_systems.android.movienight.ui.AppViewModelProvider
import com.mobile_systems.android.movienight.ui.movienightevent.AddFriendsScreen
import com.mobile_systems.android.movienight.ui.home.HomeScreen
import com.mobile_systems.android.movienight.ui.home.HomeViewModel
import com.mobile_systems.android.movienight.ui.MovieDetailsViewModel
import com.mobile_systems.android.movienight.ui.MovieViewModel
import com.mobile_systems.android.movienight.ui.movienightevent.MovieNightEventViewModel
import com.mobile_systems.android.movienight.ui.movienightevent.RankingListScreen
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.movienightevent.VoteScreen
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
    val movieViewModel: MovieViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val themeViewModel: ThemeViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val movieDetailsViewModel: MovieDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val movieNightEventViewModel: MovieNightEventViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val themeUiState by themeViewModel.uiState.collectAsState()

    MovieNightTheme(darkTheme = themeUiState.isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize())
        { NavHost(
                navController = navController,
                startDestination = MovieNightApp.Home.name,
                modifier = Modifier.safeDrawingPadding()
            ) {
                composable(route = MovieNightApp.Home.name) {
                    HomeScreen(
                        onMovieNightClicked = {
                            navController.navigate(MovieNightApp.AddFriends.name)
                            movieNightEventViewModel.resetMovieNight()
                        },
                        homeViewModel = homeViewModel,
                        modifier = Modifier,
                        themeViewModel = themeViewModel,
                        movieDetailsViewModel = movieDetailsViewModel,
                        movieViewModel = movieViewModel
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
                        modifier = Modifier,
                        themeViewModel = themeViewModel,
                        movieDetailsViewModel = movieDetailsViewModel
                    )
                }
                composable(route = MovieNightApp.RankingList.name) {
                    RankingListScreen(
                        movieNightEventViewModel = movieNightEventViewModel,
                        onHomeClicked = { navController.navigate(MovieNightApp.Home.name) },
                        onTryAgainClicked = { navController.navigate(MovieNightApp.AddFriends.name) },
                        themeViewModel = themeViewModel,
                        movieDetailsViewModel = movieDetailsViewModel
                    )
                }
            }
        }
    }
}