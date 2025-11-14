package com.example.feature.weather.data.repository

import app.cash.turbine.test
import com.example.feature.weather.data.datasource.local.WeatherLocalDataSource
import com.example.feature.weather.data.datasource.remote.WeatherRemoteDataSource
import com.example.feature.weather.data.local.database.entity.WeatherEntity
import com.example.feature.weather.data.remote.api.response.DailyResponse
import com.example.feature.weather.data.remote.api.response.TempResponse
import com.example.feature.weather.data.remote.api.response.WeatherApiResponse
import com.example.feature.weather.data.remote.api.response.WeatherConditionResponse
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class WeatherRepositoryImplTest {

    private lateinit var remoteDataSource: WeatherRemoteDataSource
    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var repository: WeatherRepositoryImpl

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        localDataSource = mockk()
        repository = WeatherRepositoryImpl(
            remoteDataSource = remoteDataSource,
            localDataSource = localDataSource
        )
    }

    @Test
    fun `getWeatherForecast should return mapped weather list from local data source`() = runTest {
        val weatherEntities = listOf(
            WeatherEntity(
                id = "1_0",
                date = 1699000000L,
                tempDay = 20.0,
                tempMin = 15.0,
                tempMax = 25.0,
                humidity = 65,
                windSpeed = 5.0,
                description = "Partly cloudy",
                icon = "02d"
            ),
            WeatherEntity(
                id = "2_1",
                date = 1699086400L,
                tempDay = 18.0,
                tempMin = 13.0,
                tempMax = 23.0,
                humidity = 70,
                windSpeed = 6.0,
                description = "Rainy",
                icon = "10d"
            )
        )

        every { localDataSource.getAllWeather() } returns flowOf(weatherEntities)

        repository.getWeatherForecast().test {
            val emittedList = awaitItem()
            assertEquals(2, emittedList.size)
            assertEquals(weatherEntities[0].id, emittedList[0].id)
            assertEquals(weatherEntities[1].id, emittedList[1].id)
            awaitComplete()
        }

        verify { localDataSource.getAllWeather() }
    }

    @Test
    fun `getWeatherForecast should return empty list when no data available`() = runTest {
        every { localDataSource.getAllWeather() } returns flowOf(emptyList())

        repository.getWeatherForecast().test {
            val emittedList = awaitItem()
            assertEquals(0, emittedList.size)
            awaitComplete()
        }

        verify { localDataSource.getAllWeather() }
    }

    @Test
    fun `getWeatherForecast should emit multiple times when local data changes`() = runTest {
        val firstList = listOf(
            WeatherEntity(
                id = "1_0",
                date = 1699000000L,
                tempDay = 20.0,
                tempMin = 15.0,
                tempMax = 25.0,
                humidity = 65,
                windSpeed = 5.0,
                description = "Sunny",
                icon = "01d"
            )
        )

        val secondList = firstList + WeatherEntity(
            id = "2_1",
            date = 1699086400L,
            tempDay = 18.0,
            tempMin = 13.0,
            tempMax = 23.0,
            humidity = 70,
            windSpeed = 6.0,
            description = "Cloudy",
            icon = "04d"
        )

        every { localDataSource.getAllWeather() } returns flowOf(firstList, secondList)

        repository.getWeatherForecast().test {
            val firstEmitted = awaitItem()
            assertEquals(1, firstEmitted.size)

            val secondEmitted = awaitItem()
            assertEquals(2, secondEmitted.size)

            awaitComplete()
        }
    }

    @Test
    fun `getWeatherById should return mapped weather entity when found`() = runTest {
        val weatherId = "1_0"
        val weatherEntity = WeatherEntity(
            id = weatherId,
            date = 1699000000L,
            tempDay = 20.0,
            tempMin = 15.0,
            tempMax = 25.0,
            humidity = 65,
            windSpeed = 5.0,
            description = "Sunny",
            icon = "01d"
        )

        every { localDataSource.getWeatherById(weatherId) } returns flowOf(weatherEntity)

        repository.getWeatherById(weatherId).test {
            val emittedWeather = awaitItem()
            assertEquals(weatherId, emittedWeather?.id)
            assertEquals(20.0, emittedWeather?.tempDay)
            assertEquals("Sunny", emittedWeather?.description)
            awaitComplete()
        }

        verify { localDataSource.getWeatherById(weatherId) }
    }

    @Test
    fun `getWeatherById should return null when weather not found`() = runTest {
        val weatherId = "nonexistent"
        every { localDataSource.getWeatherById(weatherId) } returns flowOf(null)

        repository.getWeatherById(weatherId).test {
            val emittedWeather = awaitItem()
            assertNull(emittedWeather)
            awaitComplete()
        }

        verify { localDataSource.getWeatherById(weatherId) }
    }

    @Test
    fun `getWeatherById should call local data source with correct id`() = runTest {
        val weatherId = "test_id_123"
        every { localDataSource.getWeatherById(weatherId) } returns flowOf(null)

        repository.getWeatherById(weatherId).test {
            awaitItem()
            awaitComplete()
        }

        verify { localDataSource.getWeatherById(weatherId) }
    }

    @Test
    fun `refreshWeather should fetch data and save to local datasource`() = runTest {
        val latitude = 40.4983
        val longitude = -3.5676
        val apiKey = "test_api_key"

        val remoteResponse = createMockWeatherResponse()

        coEvery {
            remoteDataSource.getWeather(latitude, longitude, apiKey)
        } returns remoteResponse

        coEvery {
            localDataSource.deleteAllWeather()
        } just Runs

        coEvery {
            localDataSource.saveWeather(any())
        } just Runs

        repository.refreshWeather(latitude, longitude, apiKey)

        coVerify {
            remoteDataSource.getWeather(latitude, longitude, apiKey)
            localDataSource.deleteAllWeather()
            localDataSource.saveWeather(any())
        }
    }

    @Test
    fun `refreshWeather should map api response to entities correctly`() = runTest {
        val latitude = 40.4983
        val longitude = -3.5676
        val apiKey = "test_api_key"

        val remoteResponse = createMockWeatherResponse()

        coEvery {
            remoteDataSource.getWeather(latitude, longitude, apiKey)
        } returns remoteResponse

        coEvery {
            localDataSource.deleteAllWeather()
        } just Runs

        val savedEntitiesSlot = slot<List<WeatherEntity>>()
        coEvery {
            localDataSource.saveWeather(capture(savedEntitiesSlot))
        } just Runs

        repository.refreshWeather(latitude, longitude, apiKey)

        val savedEntities = savedEntitiesSlot.captured
        assertEquals(2, savedEntities.size)
        assertEquals("1699000000_0", savedEntities[0].id)
        assertEquals(20.5, savedEntities[0].tempDay)
        assertEquals("Partly cloudy", savedEntities[0].description)
        assertEquals("02d", savedEntities[0].icon)
        assertEquals(1699086400L, savedEntities[1].date)
    }

    @Test
    fun `refreshWeather should handle empty weather list`() = runTest {
        val latitude = 40.4983
        val longitude = -3.5676
        val apiKey = "test_api_key"

        val emptyResponse = WeatherApiResponse(
            lat = latitude,
            lon = longitude,
            timezone = "Europe/Madrid",
            daily = emptyList()
        )

        coEvery {
            remoteDataSource.getWeather(latitude, longitude, apiKey)
        } returns emptyResponse

        coEvery {
            localDataSource.deleteAllWeather()
        } just Runs

        coEvery {
            localDataSource.saveWeather(any())
        } just Runs

        repository.refreshWeather(latitude, longitude, apiKey)

        coVerify {
            remoteDataSource.getWeather(latitude, longitude, apiKey)
            localDataSource.deleteAllWeather()
            localDataSource.saveWeather(emptyList())
        }
    }

    @Test
    fun `refreshWeather should clear old data before saving new data`() = runTest {
        val latitude = 40.4983
        val longitude = -3.5676
        val apiKey = "test_api_key"

        val remoteResponse = createMockWeatherResponse()

        coEvery {
            remoteDataSource.getWeather(latitude, longitude, apiKey)
        } returns remoteResponse

        coEvery {
            localDataSource.deleteAllWeather()
        } just Runs

        coEvery {
            localDataSource.saveWeather(any())
        } just Runs

        repository.refreshWeather(latitude, longitude, apiKey)

        coVerify(ordering = io.mockk.Ordering.SEQUENCE) {
            localDataSource.deleteAllWeather()
            localDataSource.saveWeather(any())
        }
    }

    @Test
    fun `refreshWeather should handle null weather description and icon`() = runTest {
        val latitude = 40.4983
        val longitude = -3.5676
        val apiKey = "test_api_key"

        val remoteResponse = WeatherApiResponse(
            lat = latitude,
            lon = longitude,
            timezone = "Europe/Madrid",
            daily = listOf(
                DailyResponse(
                    dt = 1699000000L,
                    temp = TempResponse(day = 20.5, min = 15.0, max = 25.0),
                    humidity = 65,
                    wind_speed = 5.0,
                    weather = emptyList()
                )
            )
        )

        coEvery {
            remoteDataSource.getWeather(latitude, longitude, apiKey)
        } returns remoteResponse

        coEvery {
            localDataSource.deleteAllWeather()
        } just Runs

        val savedEntitiesSlot = slot<List<WeatherEntity>>()
        coEvery {
            localDataSource.saveWeather(capture(savedEntitiesSlot))
        } just Runs

        repository.refreshWeather(latitude, longitude, apiKey)

        val savedEntities = savedEntitiesSlot.captured
        assertEquals("", savedEntities[0].description)
        assertEquals("", savedEntities[0].icon)
    }

    @Test
    fun `refreshWeather should use correct coordinates`() = runTest {
        val latitude = 40.4983
        val longitude = -3.5676
        val apiKey = "test_api_key"

        coEvery {
            remoteDataSource.getWeather(latitude, longitude, apiKey)
        } returns createMockWeatherResponse()

        coEvery {
            localDataSource.deleteAllWeather()
        } just Runs

        coEvery {
            localDataSource.saveWeather(any())
        } just Runs

        repository.refreshWeather(latitude, longitude, apiKey)

        coVerify {
            remoteDataSource.getWeather(40.4983, -3.5676, apiKey)
        }
    }

    @Test
    fun `refreshWeather should generate correct entity ids based on index`() = runTest {
        val latitude = 40.4983
        val longitude = -3.5676
        val apiKey = "test_api_key"

        val remoteResponse = createMockWeatherResponse()

        coEvery {
            remoteDataSource.getWeather(latitude, longitude, apiKey)
        } returns remoteResponse

        coEvery {
            localDataSource.deleteAllWeather()
        } just Runs

        val savedEntitiesSlot = slot<List<WeatherEntity>>()
        coEvery {
            localDataSource.saveWeather(capture(savedEntitiesSlot))
        } just Runs

        repository.refreshWeather(latitude, longitude, apiKey)

        val savedEntities = savedEntitiesSlot.captured
        assertEquals("1699000000_0", savedEntities[0].id)
        assertEquals("1699086400_1", savedEntities[1].id)
    }

    private fun createMockWeatherResponse(): WeatherApiResponse {
        return WeatherApiResponse(
            lat = 40.4983,
            lon = -3.5676,
            timezone = "Europe/Madrid",
            daily = listOf(
                DailyResponse(
                    dt = 1699000000L,
                    temp = TempResponse(day = 20.5, min = 15.0, max = 25.0),
                    humidity = 65,
                    wind_speed = 5.0,
                    weather = listOf(
                        WeatherConditionResponse(
                            main = "Clouds",
                            description = "Partly cloudy",
                            icon = "02d"
                        )
                    )
                ),
                DailyResponse(
                    dt = 1699086400L,
                    temp = TempResponse(day = 18.5, min = 13.0, max = 23.0),
                    humidity = 70,
                    wind_speed = 6.0,
                    weather = listOf(
                        WeatherConditionResponse(
                            main = "Rain",
                            description = "Rainy",
                            icon = "10d"
                        )
                    )
                )
            )
        )
    }
}