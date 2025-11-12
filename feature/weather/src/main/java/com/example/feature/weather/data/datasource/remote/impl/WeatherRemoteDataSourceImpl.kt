package com.example.feature.weather.data.datasource.remote.impl

import com.example.feature.weather.data.datasource.remote.WeatherRemoteDataSource
import com.example.feature.weather.data.remote.api.WeatherApi
import com.example.feature.weather.data.remote.api.response.WeatherApiResponse
import javax.inject.Inject

class WeatherRemoteDataSourceImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : WeatherRemoteDataSource {
    override suspend fun getWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): WeatherApiResponse {
        return weatherApi.getWeather(
            latitude = latitude,
            longitude = longitude,
            apiKey = apiKey
        )
    }
}
