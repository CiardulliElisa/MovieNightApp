package com.mobile_systems.android.movienight

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import com.mobile_systems.android.movienight.ui.home.MovieViewModel
import com.mobile_systems.android.movienight.ui.movienightevent.MovieNightEventViewModel
import com.mobile_systems.android.movienight.ui.movienightevent.RankingListScreen
import com.mobile_systems.android.movienight.ui.ThemeViewModel
import com.mobile_systems.android.movienight.ui.movienightevent.VoteScreen
import com.mobile_systems.android.movienight.ui.theme.MovieNightTheme
import com.mobile_systems.android.movienight.ui.utils.MovieNightContentType

enum class MovieNightApp {
    Home(),
    AddFriends(),
    Vote(),
    RankingList(),
}

@Composable
fun MovieNightApp(
    navController: NavHostController = rememberNavController(),
    windowSize: WindowWidthSizeClass
) {

    //Create view models using the view model provider
    val movieViewModel: MovieViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val movieDetailsViewModel: MovieDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val movieNightEventViewModel: MovieNightEventViewModel = viewModel(factory = AppViewModelProvider.Factory)

    val themeViewModel: ThemeViewModel = viewModel()
    val themeUiState by themeViewModel.uiState.collectAsState()

    // Determine the content type to display based on the window size
    val contentType: MovieNightContentType = when (windowSize) {
        WindowWidthSizeClass.Compact -> {
            MovieNightContentType.LIST_ONLY
        }
        WindowWidthSizeClass.Medium -> {
            MovieNightContentType.LIST_ONLY
        }
        WindowWidthSizeClass.Expanded -> {
            MovieNightContentType.LIST_AND_DETAIL
        }
        else -> {
            MovieNightContentType.LIST_ONLY
        }
    }

    //The theme for the entire app is determined
    MovieNightTheme(darkTheme = themeUiState.isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize())
        {
            //Define the various destinations for the navigation
            NavHost(
                navController = navController,
                startDestination = MovieNightApp.Home.name,
                modifier = Modifier.safeDrawingPadding()
            ) {
                // From the home page navigate to the add friends page and reset the movie night event to prepare for a new one
                composable(route = MovieNightApp.Home.name) {
                    HomeScreen(
                        contentType = contentType,
                        onMovieNightClicked = {
                            navController.navigate(MovieNightApp.AddFriends.name)
                            movieNightEventViewModel.resetMovieNight()
                        },
                        homeViewModel = homeViewModel,
                        modifier = Modifier,
                        themeViewModel = themeViewModel,
                        movieDetailsViewModel = movieDetailsViewModel,
                        movieViewModel = movieViewModel,
                    )
                }
                //When a new movie night is ready navigate to the vote page
                // Also navigate back to the home page
                composable(route = MovieNightApp.AddFriends.name) {
                    AddFriendsScreen(
                        onStartClicked = {movieNightEventViewModel.startMovieNightEvent() },
                        onNavigateToVote = { navController.navigate(MovieNightApp.Vote.name) },
                        onBackClicked = { navController.popBackStack() },
                        movieNightEventViewModel = movieNightEventViewModel,
                        themeViewModel = themeViewModel,
                        modifier = Modifier,
                    )
                }
                //From the vote page navigate back to the add friends page, reset the movie night event to prepare for a new one
                // Navigate to the final rankings page if the movie night is finished
                // Navigate back to the add friends page or the home page through the navigation buttons
                // Once the movie night is finished all information is lost about the vote pages and add friends page
                composable(route = MovieNightApp.Vote.name) {
                    BackHandler {
                        movieNightEventViewModel.resetMovieNight()
                        navController.popBackStack()
                    }
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
                        movieDetailsViewModel = movieDetailsViewModel,
                        contentType = contentType
                    )
                }
                //From the final rankings page navigate back to the home page or the add friends page to start a new movie night
                composable(route = MovieNightApp.RankingList.name) {
                    RankingListScreen(
                        movieNightEventViewModel = movieNightEventViewModel,
                        onHomeClicked = { navController.navigate(MovieNightApp.Home.name) },
                        onTryAgainClicked = { navController.navigate(MovieNightApp.AddFriends.name) },
                        themeViewModel = themeViewModel,
                        movieDetailsViewModel = movieDetailsViewModel,
                        contentType = contentType
                    )
                }
            }
        }
    }
}