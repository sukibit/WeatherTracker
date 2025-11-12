package com.example.feature.weather.data.remote.api.response

data class DailyResponse(
    val dt: Long,
    val temp: TempResponse,
    val humidity: Int,
    val wind_speed: Double,
    val weather: List<WeatherConditionResponse>
)