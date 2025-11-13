package com.example.feature.weather.domain.model

data class Weather(
    val id: String,
    val date: Long,
    val tempDay: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val windSpeed: Double,
    val description: String,
    val icon: String
)