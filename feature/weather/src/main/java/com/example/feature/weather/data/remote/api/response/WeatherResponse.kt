package com.example.feature.weather.data.remote.api.response

data class WeatherApiResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val daily: List<DailyResponse>
)