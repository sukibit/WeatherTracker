package com.example.feature.weather.domain.usecase

import app.cash.turbine.test
import com.example.feature.weather.domain.model.Weather
import com.example.feature.weather.domain.repository.WeatherRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetWeatherDetailUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetWeatherDetailUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetWeatherDetailUseCase(repository)
    }

    @Test
    fun `invoke should wrap weather result in Result_success`() = runTest {
        val weatherId = "1_0"
        val weather = Weather(
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

        every { repository.getWeatherById(weatherId) } returns flowOf(weather)

        useCase.invoke(weatherId).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(weather, result.getOrNull())
            awaitComplete()
        }

        verify { repository.getWeatherById(weatherId) }
    }

    @Test
    fun `invoke should wrap null in Result_success`() = runTest {
        val weatherId = "nonexistent"
        every { repository.getWeatherById(weatherId) } returns flowOf(null)

        useCase.invoke(weatherId).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(null, result.getOrNull())
            awaitComplete()
        }

        verify { repository.getWeatherById(weatherId) }
    }

    @Test
    fun `invoke should catch exception from repository and emit Result_failure`() = runTest {
        val weatherId = "1_0"
        val exception = RuntimeException("Repository error")

        every { repository.getWeatherById(weatherId) } throws exception

        useCase.invoke(weatherId).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals("Repository error", result.exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit multiple results when repository emits multiple values`() = runTest {
        val weatherId = "1_0"
        val weather1 = Weather(
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
        val weather2 = weather1.copy(tempDay = 21.0, description = "Partly cloudy")

        every { repository.getWeatherById(weatherId) } returns flowOf(weather1, weather2)

        useCase.invoke(weatherId).test {
            val firstResult = awaitItem()
            assertTrue(firstResult.isSuccess)
            assertEquals(weather1, firstResult.getOrNull())

            val secondResult = awaitItem()
            assertTrue(secondResult.isSuccess)
            assertEquals(weather2, secondResult.getOrNull())

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle IllegalArgumentException from repository`() = runTest {
        val weatherId = "1_0"
        val exception = IllegalArgumentException("Invalid weather ID")

        every { repository.getWeatherById(weatherId) } throws exception

        useCase.invoke(weatherId).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should wrap NullPointerException in Result_failure`() = runTest {
        val weatherId = "1_0"
        val exception = NullPointerException("Null value from repository")

        every { repository.getWeatherById(weatherId) } throws exception

        useCase.invoke(weatherId).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals("Null value from repository", result.exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle empty flow from repository`() = runTest {
        val weatherId = "1_0"
        every { repository.getWeatherById(weatherId) } returns flowOf()

        useCase.invoke(weatherId).test {
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit success for each value emitted by repository`() = runTest {
        val weatherId = "1_0"
        val weather1 = createWeather(id = "weather_1", tempDay = 15.0)
        val weather2 = createWeather(id = "weather_2", tempDay = 18.0)
        val weather3 = createWeather(id = "weather_3", tempDay = 22.0)

        every { repository.getWeatherById(weatherId) } returns flowOf(weather1, weather2, weather3)

        useCase.invoke(weatherId).test {
            repeat(3) {
                val result = awaitItem()
                assertTrue(result.isSuccess)
            }
            awaitComplete()
        }
    }

    @Test
    fun `invoke should call repository with correct weather id`() = runTest {
        val weatherId = "test_id_12345"
        every { repository.getWeatherById(weatherId) } returns flowOf(null)

        useCase.invoke(weatherId).test {
            awaitItem()
            awaitComplete()
        }

        verify { repository.getWeatherById(weatherId) }
    }

    @Test
    fun `invoke should properly convert exception message in Result_failure`() = runTest {
        val weatherId = "1_0"
        val errorMessage = "Network connection failed"
        val exception = RuntimeException(errorMessage)

        every { repository.getWeatherById(weatherId) } throws exception

        useCase.invoke(weatherId).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            val thrownException = result.exceptionOrNull()
            assertEquals(errorMessage, thrownException?.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should wrap data with success even if data is Weather with minimal values`() = runTest {
        val weatherId = "1_0"
        val weather = Weather(
            id = weatherId,
            date = 0L,
            tempDay = 0.0,
            tempMin = 0.0,
            tempMax = 0.0,
            humidity = 0,
            windSpeed = 0.0,
            description = "",
            icon = ""
        )

        every { repository.getWeatherById(weatherId) } returns flowOf(weather)

        useCase.invoke(weatherId).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(weather, result.getOrNull())
            awaitComplete()
        }
    }

    private fun createWeather(
        id: String = "default",
        tempDay: Double = 20.0
    ): Weather {
        return Weather(
            id = id,
            date = 1699000000L,
            tempDay = tempDay,
            tempMin = tempDay - 5.0,
            tempMax = tempDay + 5.0,
            humidity = 65,
            windSpeed = 5.0,
            description = "Sunny",
            icon = "01d"
        )
    }
}