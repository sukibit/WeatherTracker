package com.example.feature.weather.presentation.contract

import com.example.core.viewmodel.UIEffect
import com.example.core.viewmodel.UIEvent
import com.example.core.viewmodel.UIState
import com.example.feature.weather.presentation.model.WeatherUi

interface WeatherDetailContract {
    data class State(
        val isLoading: Boolean = true,
        val weather: WeatherUi? = null,
        val showError: Boolean = false,
        val errorMessage: String = ""
    ) : UIState

    sealed interface Event : UIEvent {
        data class OnInit(val weatherId: String) : Event
        data object OnErrorDismissed : Event
        data object OnBackClicked : Event
    }

    sealed interface Effect : UIEffect {
        data object NavigateBack : Effect
    }
}