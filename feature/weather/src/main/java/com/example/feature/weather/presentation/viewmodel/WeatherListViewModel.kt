package com.example.feature.weather.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.core.data.WeatherConfig
import com.example.core.viewmodel.ViewModel
import com.example.feature.weather.domain.model.RefreshWeatherParams
import com.example.feature.weather.domain.usecase.GetWeatherForecastUseCase
import com.example.feature.weather.domain.usecase.RefreshWeatherUseCase
import com.example.feature.weather.presentation.contract.WeatherListContract
import com.example.feature.weather.presentation.model.WeatherUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherListViewModel @Inject constructor(
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase
) : ViewModel<WeatherListContract.Event, WeatherListContract.State, WeatherListContract.Effect>() {

    private companion object {
        const val MADRID_LAT = 40.4983
        const val MADRID_LON = -3.5676
    }

    private var isInitialized = false

    override fun createInitialState() = WeatherListContract.State()

    override fun handleEvent(event: WeatherListContract.Event) {
        when (event) {
            is WeatherListContract.Event.OnInit -> {
                if (!isInitialized) {
                    isInitialized = true
                    observeWeatherData()
                    refreshWeather()
                }
            }

            is WeatherListContract.Event.OnWeatherClicked -> {
                setEffect { WeatherListContract.Effect.NavigateToDetail(event.weatherId) }
            }

            is WeatherListContract.Event.OnErrorDismissed -> {
                setState { copy(showError = false, errorMessage = "") }
            }
        }
    }

    private fun observeWeatherData() {
        viewModelScope.launch {
            getWeatherForecastUseCase().collect { result ->
                result.fold(onSuccess = { weather ->
                    val weatherUiList = WeatherUiMapper.mapWeatherListToUi(weather)
                    setState {
                        copy(
                            isLoading = false,
                            weather = weatherUiList,
                            showError = false
                        )
                    }
                }, onFailure = { error ->
                    setState {
                        copy(
                            isLoading = false,
                            showError = true,
                            errorMessage = error.message.orEmpty()
                        )
                    }
                })
            }
        }
    }

    private fun refreshWeather() {
        viewModelScope.launch {
            setState { copy(isRefreshing = true) }

            val result = refreshWeatherUseCase(
                RefreshWeatherParams(
                    latitude = MADRID_LAT,
                    longitude = MADRID_LON,
                    apiKey = WeatherConfig.apiKey
                )
            )

            result.fold(
                onSuccess = {
                    setState { copy(isRefreshing = false) }
                },
                onFailure = { error ->
                    setState {
                        copy(
                            isRefreshing = false,
                            showError = true,
                            errorMessage = error.message.orEmpty()
                        )
                    }
                }
            )
        }
    }
}