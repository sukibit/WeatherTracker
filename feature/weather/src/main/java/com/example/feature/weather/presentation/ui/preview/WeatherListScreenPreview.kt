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
import com.example.feature.weather.presentation.model.WeatherUi
import com.example.feature.weather.presentation.ui.components.WeatherCard

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

private val previewWeatherRainy = WeatherUi(
    id = "2",
    date = "2025-01-21",
    tempDay = "16°C",
    tempMin = "10°C",
    tempMax = "20°C",
    humidity = "80%",
    windSpeed = "22 km/h",
    description = "Rainy weather with strong winds expected throughout the day",
    iconUrl = "https://openweathermap.org/img/wn/10d@2x.png"
)

private val previewWeatherSunny = WeatherUi(
    id = "3",
    date = "2025-01-22",
    tempDay = "22°C",
    tempMin = "16°C",
    tempMax = "26°C",
    humidity = "40%",
    windSpeed = "8 km/h",
    description = "Sunny and clear skies",
    iconUrl = "https://openweathermap.org/img/wn/01d@2x.png"
)

@Composable
private fun MockWeatherIcon() {
    Box(
        modifier = Modifier
            .size(Dimens.IconLarge)
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text("☁️", style = MaterialTheme.typography.displayMedium)
    }
}

@Preview(showBackground = true, name = "Weather Card - Partly Cloudy")
@Composable
fun WeatherCardPreview() {
    WeatherTrackerTheme {
        WeatherCard(
            weather = previewWeather,
            onClick = {},
            iconContent = { MockWeatherIcon() }
        )
    }
}

@Preview(showBackground = true, name = "Weather Card - Rainy")
@Composable
fun WeatherCardRainyPreview() {
    WeatherTrackerTheme {
        WeatherCard(
            weather = previewWeatherRainy,
            onClick = {},
            iconContent = { MockWeatherIcon() }
        )
    }
}

@Preview(showBackground = true, name = "Weather Card - Sunny")
@Composable
fun WeatherCardSunnyPreview() {
    WeatherTrackerTheme {
        WeatherCard(
            weather = previewWeatherSunny,
            onClick = {},
            iconContent = { MockWeatherIcon() }
        )
    }
}

@Preview(showBackground = true, name = "Weather Card - Long Description")
@Composable
fun WeatherCardLongDescriptionPreview() {
    WeatherTrackerTheme {
        WeatherCard(
            weather = previewWeatherRainy.copy(
                description = "Rainy weather with strong winds expected throughout the day with possible thunderstorms in the evening"
            ),
            onClick = {},
            iconContent = { MockWeatherIcon() }
        )
    }
}