package com.example.feature.weather.data.repository

import com.example.feature.weather.data.datasource.local.WeatherLocalDataSource
import com.example.feature.weather.data.datasource.remote.WeatherRemoteDataSource
import com.example.feature.weather.data.local.database.entity.WeatherEntity
import com.example.feature.weather.data.mapper.toDomain
import com.example.feature.weather.domain.model.Weather
import com.example.feature.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) : WeatherRepository {

    override fun getWeatherForecast(): Flow<List<Weather>> {
        return localDataSource.getAllWeather().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun refreshWeather(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ) {
        val response = remoteDataSource.getWeather(latitude, longitude, apiKey)
        val weatherEntities = response.daily.mapIndexed { index, daily ->
            WeatherEntity(
                id = "${daily.dt}_$index",
                date = daily.dt,
                tempDay = daily.temp.day,
                tempMin = daily.temp.min,
                tempMax = daily.temp.max,
                humidity = daily.humidity,
                windSpeed = daily.wind_speed,
                description = daily.weather.firstOrNull()?.description.orEmpty(),
                icon = daily.weather.firstOrNull()?.icon.orEmpty()
            )
        }

        localDataSource.deleteAllWeather()
        localDataSource.saveWeather(weatherEntities)
    }
}
