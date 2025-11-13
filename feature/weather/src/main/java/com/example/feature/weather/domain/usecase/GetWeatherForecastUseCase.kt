package com.example.feature.weather.domain.usecase

import com.example.feature.weather.domain.BaseFlowUseCase
import com.example.feature.weather.domain.model.Weather
import com.example.feature.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetWeatherForecastUseCase @Inject constructor(
    private val repository: WeatherRepository
) : BaseFlowUseCase<List<Weather>, Unit>() {

    override fun execute(params: Unit): Flow<List<Weather>> {
        return repository.getWeatherForecast()
    }
}