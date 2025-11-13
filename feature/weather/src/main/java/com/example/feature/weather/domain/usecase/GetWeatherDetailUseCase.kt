package com.example.feature.weather.domain.usecase

import com.example.feature.weather.domain.BaseFlowUseCase
import com.example.feature.weather.domain.model.Weather
import com.example.feature.weather.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherDetailUseCase @Inject constructor(
    private val repository: WeatherRepository
) : BaseFlowUseCase<Weather?, String>() {

    override fun execute(params: String): Flow<Weather?> {
        return repository.getWeatherById(params)
    }
}
