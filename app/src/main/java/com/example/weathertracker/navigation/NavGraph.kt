package com.example.weathertracker.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.feature.weather.presentation.ui.screens.WeatherDetailScreen
import com.example.feature.weather.presentation.ui.screens.WeatherListScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavDestinations.WEATHER_LIST_ROUTE
    ) {
        composable(route = NavDestinations.WEATHER_LIST_ROUTE) {
            WeatherListScreen(navController = navController)
        }
        composable(
            route = "${NavDestinations.WEATHER_DETAIL_ROUTE}/{${NavDestinations.WEATHER_ID_ARG}}",
            arguments = listOf(
                navArgument(NavDestinations.WEATHER_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val weatherId = backStackEntry.arguments?.getString(NavDestinations.WEATHER_ID_ARG).orEmpty()
            WeatherDetailScreen(
                navController = navController,
                weatherId = weatherId
            )
        }
    }
}