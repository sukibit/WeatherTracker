package com.example.feature.weather.domain.di

import com.example.feature.weather.domain.repository.WeatherRepository
import com.example.feature.weather.domain.usecase.GetWeatherDetailUseCase
import com.example.feature.weather.domain.usecase.GetWeatherForecastUseCase
import com.example.feature.weather.domain.usecase.RefreshWeatherUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideGetWeatherForecastUseCase(
        repository: WeatherRepository
    ): GetWeatherForecastUseCase {
        return GetWeatherForecastUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideRefreshWeatherUseCase(
        repository: WeatherRepository
    ): RefreshWeatherUseCase {
        return RefreshWeatherUseCase(repository)
    }

    @Singleton
    @Provides
    fun provideGetWeatherDetailUseCase(
        repository: WeatherRepository
    ): GetWeatherDetailUseCase {
        return GetWeatherDetailUseCase(repository)
    }
}
