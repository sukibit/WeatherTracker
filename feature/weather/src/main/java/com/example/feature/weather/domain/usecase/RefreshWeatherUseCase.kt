package com.example.feature.weather.domain.usecase

import com.example.core.usecase.BaseSuspendUseCase
import com.example.feature.weather.domain.model.RefreshWeatherParams
import com.example.feature.weather.domain.repository.WeatherRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshWeatherUseCase @Inject constructor(
    private val repository: WeatherRepository
) : BaseSuspendUseCase<Unit, RefreshWeatherParams>() {

    override suspend fun execute(params: RefreshWeatherParams) {
        repository.refreshWeather(
            latitude = params.latitude,
            longitude = params.longitude,
            apiKey = params.apiKey
        )
    }
}