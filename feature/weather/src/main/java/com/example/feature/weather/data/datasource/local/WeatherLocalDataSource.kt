package com.example.feature.weather.data.datasource.local

import com.example.feature.weather.data.local.database.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSource {
    fun getAllWeather(): Flow<List<WeatherEntity>>
    fun getWeatherById(id: String): Flow<WeatherEntity?>
    suspend fun saveWeather(weather: List<WeatherEntity>)
    suspend fun deleteAllWeather()
}