package com.example.feature.weather.data.mapper

import com.example.feature.weather.data.local.database.entity.WeatherEntity
import com.example.feature.weather.domain.model.Weather

fun WeatherEntity.toDomain(): Weather {
    return Weather(
        id = id,
        date = date,
        tempDay = tempDay,
        tempMin = tempMin,
        tempMax = tempMax,
        humidity = humidity,
        windSpeed = windSpeed,
        description = description,
        icon = icon
    )
}

fun Weather.toEntity(): WeatherEntity {
    return WeatherEntity(
        id = id,
        date = date,
        tempDay = tempDay,
        tempMin = tempMin,
        tempMax = tempMax,
        humidity = humidity,
        windSpeed = windSpeed,
        description = description,
        icon = icon
    )
}
