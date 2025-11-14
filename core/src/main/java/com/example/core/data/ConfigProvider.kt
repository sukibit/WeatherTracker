package com.example.core.data

import com.example.core.BuildConfig

object WeatherConfig {
    val apiKey: String
        get() = BuildConfig.OPENWEATHER_API_KEY

    const val BASE_URL = "https://api.openweathermap.org/"
}