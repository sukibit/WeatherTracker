package com.example.feature.weather.presentation.ui.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.example.core.ui.Dimens
import com.example.core.ui.WeatherTrackerTheme
import com.example.feature.weather.presentation.contract.WeatherDetailContract
import com.example.feature.weather.presentation.model.WeatherUi
import com.example.feature.weather.presentation.ui.screens.WeatherDetailBody
import com.example.feature.weather.presentation.ui.screens.WeatherDetailContent
import com.example.feature.weather.presentation.ui.screens.WeatherIconCard
import com.example.feature.weather.presentation.ui.screens.EmptyStateWeatherDetailView

private val previewWeather = WeatherUi(
    id = "1",
    date = "2025-01-20",
    tempDay = "18°C",
    tempMin = "12°C",
    tempMax = "22°C",
    humidity = "60%",
    windSpeed = "14 km/h",
    description = "Partly cloudy with a chance of rain",
    iconUrl = "https://openweathermap.org/img/wn/03d@2x.png"
)

@Composable
private fun MockWeatherIcon() {
    Box(
        modifier = Modifier
            .size(Dimens.IconXXLarge)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text("☁️", style = MaterialTheme.typography.displayLarge)
    }
}

@Preview(showBackground = true, name = "Weather Detail - Loading")
@Composable
fun WeatherDetailLoadingPreview() {
    WeatherTrackerTheme {
        WeatherDetailContent(
            state = WeatherDetailContract.State(
                isLoading = true,
                showError = false,
                weather = null,
                errorMessage = ""
            ),
            onEventSend = {}
        )
    }
}

@Preview(showBackground = true, name = "Weather Detail - Error")
@Composable
fun WeatherDetailErrorPreview() {
    WeatherTrackerTheme {
        WeatherDetailContent(
            state = WeatherDetailContract.State(
                isLoading = false,
                showError = true,
                weather = null,
                errorMessage = "Failed to load weather details"
            ),
            onEventSend = {}
        )
    }
}

@Preview(showBackground = true, name = "Weather Detail - Empty")
@Composable
fun WeatherDetailEmptyPreview() {
    WeatherTrackerTheme {
        WeatherDetailContent(
            state = WeatherDetailContract.State(
                isLoading = false,
                showError = false,
                weather = null,
                errorMessage = ""
            ),
            onEventSend = {}
        )
    }
}

@Preview(showBackground = true, name = "Weather Body")
@Composable
fun WeatherDetailBodyPreview() {
    WeatherTrackerTheme {
        WeatherDetailBody(
            weather = previewWeather,
            iconContent = { MockWeatherIcon() }
        )
    }
}

@Preview(showBackground = true, name = "Weather Icon Card")
@Composable
fun WeatherIconCardPreview() {
    WeatherTrackerTheme {
        WeatherIconCard(
            weather = previewWeather,
            iconContent = { MockWeatherIcon() }
        )
    }
}

@Preview(showBackground = true, name = "Empty State")
@Composable
fun EmptyStateWeatherDetailViewPreview() {
    WeatherTrackerTheme {
        EmptyStateWeatherDetailView()
    }
}