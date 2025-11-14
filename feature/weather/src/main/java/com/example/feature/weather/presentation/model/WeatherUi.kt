package com.example.feature.weather.presentation.model

import com.example.feature.weather.domain.model.Weather
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class WeatherUi(
    val id: String,
    val date: String,
    val tempDay: String,
    val tempMin: String,
    val tempMax: String,
    val humidity: String,
    val windSpeed: String,
    val description: String,
    val iconUrl: String
)

object WeatherUiMapper {
    fun mapWeatherToUi(weather: Weather): WeatherUi {
        return WeatherUi(
            id = weather.id,
            date = formatDate(weather.date),
            tempDay = formatTemperature(weather.tempDay),
            tempMin = formatTemperature(weather.tempMin),
            tempMax = formatTemperature(weather.tempMax),
            humidity = formatHumidity(weather.humidity),
            windSpeed = formatWindSpeed(weather.windSpeed),
            description = weather.description.replaceFirstChar { it.uppercase() },
            iconUrl = getWeatherIconUrl(weather.icon)
        )
    }

    fun mapWeatherListToUi(
        weatherList: List<Weather>
    ): List<WeatherUi> {
        return weatherList.map { mapWeatherToUi(it) }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
    }

    private fun formatTemperature(temp: Double): String {
        return "%.1f°C".format(temp)
    }

    private fun formatHumidity(humidity: Int): String {
        return "$humidity%"
    }

    private fun formatWindSpeed(windSpeed: Double): String {
        return "%.1f m/s".format(windSpeed)
    }

    private fun getWeatherIconUrl(icon: String): String {
        return "https://openweathermap.org/img/wn/$icon@2x.png"
    }
}