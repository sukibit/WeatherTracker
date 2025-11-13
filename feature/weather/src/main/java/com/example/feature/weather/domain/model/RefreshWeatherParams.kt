package com.example.feature.weather.domain.model

data class RefreshWeatherParams(
    val latitude: Double,
    val longitude: Double,
    val apiKey: String
)