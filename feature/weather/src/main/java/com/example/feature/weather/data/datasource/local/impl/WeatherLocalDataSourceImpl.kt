package com.example.feature.weather.data.datasource.local.impl

import com.example.feature.weather.data.datasource.local.WeatherLocalDataSource
import com.example.feature.weather.data.local.database.dao.WeatherDao
import com.example.feature.weather.data.local.database.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WeatherLocalDataSourceImpl @Inject constructor(
    private val weatherDao: WeatherDao
): WeatherLocalDataSource {

    override fun getAllWeather(): Flow<List<WeatherEntity>> {
        return weatherDao.getAllWeather()
    }

    override fun getWeatherById(id: String): Flow<WeatherEntity?> {
        return weatherDao.getWeatherById(id)
    }

    override suspend fun saveWeather(weather: List<WeatherEntity>) {
        weatherDao.insertWeather(weather)
    }

    override suspend fun deleteAllWeather() {
        weatherDao.deleteAllWeather()
    }
}
