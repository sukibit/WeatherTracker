package com.example.feature.weather.data.di

import com.example.feature.weather.data.datasource.local.WeatherLocalDataSource
import com.example.feature.weather.data.datasource.local.impl.WeatherLocalDataSourceImpl
import com.example.feature.weather.data.datasource.remote.WeatherRemoteDataSource
import com.example.feature.weather.data.datasource.remote.impl.WeatherRemoteDataSourceImpl
import com.example.feature.weather.data.local.database.dao.WeatherDao
import com.example.feature.weather.data.remote.api.WeatherApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Singleton
    @Provides
    fun provideWeatherRemoteDataSource(weatherApi: WeatherApi): WeatherRemoteDataSource {
        return WeatherRemoteDataSourceImpl(weatherApi)
    }

    @Singleton
    @Provides
    fun provideWeatherLocalDataSource(weatherDao: WeatherDao): WeatherLocalDataSource {
        return WeatherLocalDataSourceImpl(weatherDao)
    }
}