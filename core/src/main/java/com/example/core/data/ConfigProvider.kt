package com.example.core.data

import com.example.core.BuildConfig

object WeatherConfig {
    val apiKey: String
        get() {
            val key = BuildConfig.OPENWEATHER_API_KEY
            return key
        }

    const val BASE_URL = "https://api.openweathermap.org/"
}