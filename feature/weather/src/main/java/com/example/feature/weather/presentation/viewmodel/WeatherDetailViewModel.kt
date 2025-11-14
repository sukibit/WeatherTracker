package com.example.feature.weather.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.core.viewmodel.ViewModel
import com.example.feature.weather.domain.usecase.GetWeatherDetailUseCase
import com.example.feature.weather.presentation.contract.WeatherDetailContract
import com.example.feature.weather.presentation.model.WeatherUiMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherDetailViewModel @Inject constructor(
    private val getWeatherDetailUseCase: GetWeatherDetailUseCase
) : ViewModel<WeatherDetailContract.Event, WeatherDetailContract.State, WeatherDetailContract.Effect>() {

    override fun createInitialState() = WeatherDetailContract.State()

    override fun handleEvent(event: WeatherDetailContract.Event) {
        when (event) {
            is WeatherDetailContract.Event.OnInit -> loadWeatherDetail(event.weatherId)

            is WeatherDetailContract.Event.OnErrorDismissed -> {
                setState { copy(showError = false, errorMessage = "") }
            }

            is WeatherDetailContract.Event.OnBackClicked -> {
                setEffect { WeatherDetailContract.Effect.NavigateBack }
            }
        }
    }

    private fun loadWeatherDetail(weatherId: String) {
        viewModelScope.launch {
            getWeatherDetailUseCase(weatherId).collect { result ->
                result.fold(
                    onSuccess = { weather ->
                        weather?.let {
                            setState {
                                copy(
                                    isLoading = false,
                                    weather = WeatherUiMapper.mapWeatherToUi(it),
                                    showError = false
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        setState {
                            copy(
                                isLoading = false,
                                showError = true,
                                errorMessage = error.message.orEmpty()
                            )
                        }
                    }
                )
            }
        }
    }
}