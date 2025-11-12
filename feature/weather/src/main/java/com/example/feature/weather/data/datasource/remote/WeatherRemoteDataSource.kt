package com.example.feature.weather.data.datasource.remote

import com.example.feature.weather.data.remote.api.response.WeatherApiResponse

interface WeatherRemoteDataSource {
    suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): WeatherApiResponse
}