package com.example.feature.weather.presentation.contract

import com.example.core.viewmodel.UIEffect
import com.example.core.viewmodel.UIEvent
import com.example.core.viewmodel.UIState
import com.example.feature.weather.presentation.model.WeatherUi

interface WeatherListContract {
    data class State(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val weather: List<WeatherUi> = emptyList(),
        val showError: Boolean = false,
        val errorMessage: String = ""
    ) : UIState

    sealed interface Event : UIEvent {
        data object OnInit : Event
        data class OnWeatherClicked(val weatherId: String) : Event
        data object OnErrorDismissed : Event
    }

    sealed interface Effect : UIEffect {
        data class NavigateToDetail(val weatherId: String) : Effect
    }
}