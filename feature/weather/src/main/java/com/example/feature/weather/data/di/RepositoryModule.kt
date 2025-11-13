package com.example.feature.weather.data.di

import com.example.feature.weather.data.datasource.local.WeatherLocalDataSource
import com.example.feature.weather.data.datasource.remote.WeatherRemoteDataSource
import com.example.feature.weather.data.repository.WeatherRepositoryImpl
import com.example.feature.weather.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideWeatherRepository(
        remoteDataSource: WeatherRemoteDataSource,
        localDataSource: WeatherLocalDataSource
    ): WeatherRepository {
        return WeatherRepositoryImpl(remoteDataSource, localDataSource)
    }
}