package com.example.feature.weather.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.feature.weather.data.local.database.dao.WeatherDao
import com.example.feature.weather.data.local.database.entity.WeatherEntity

@Database(
    entities = [WeatherEntity::class],
    version = 1,
    exportSchema = false
)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}