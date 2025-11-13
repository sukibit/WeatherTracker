package com.example.feature.weather.domain.repository

import com.example.feature.weather.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getWeatherForecast(): Flow<List<Weather>>
    fun getWeatherById(id: String): Flow<Weather?>
    suspend fun refreshWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String
    )
}