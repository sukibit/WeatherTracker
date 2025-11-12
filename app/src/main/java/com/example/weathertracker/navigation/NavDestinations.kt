package com.example.weathertracker.navigation

object NavDestinations {
    const val WEATHER_LIST_ROUTE = "weather_list"
    const val WEATHER_DETAIL_ROUTE = "weather_detail"
    const val WEATHER_ID_ARG = "weatherId"

    fun weatherDetailRoute(weatherId: String) = "$WEATHER_DETAIL_ROUTE/$weatherId"
}
