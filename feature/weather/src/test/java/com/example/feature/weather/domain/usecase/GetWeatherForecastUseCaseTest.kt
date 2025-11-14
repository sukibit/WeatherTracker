package com.example.feature.weather.domain.usecase

import app.cash.turbine.test
import com.example.feature.weather.domain.model.Weather
import com.example.feature.weather.domain.repository.WeatherRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetWeatherForecastUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: GetWeatherForecastUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetWeatherForecastUseCase(repository)
    }

    @Test
    fun `invoke should wrap weather list in Result_success`() = runTest {
        val weatherList = listOf(
            createWeather(id = "1_0", tempDay = 20.0),
            createWeather(id = "2_1", tempDay = 18.0)
        )

        every { repository.getWeatherForecast() } returns flowOf(weatherList)

        useCase.invoke(null).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(weatherList, result.getOrNull())
            awaitComplete()
        }

        verify { repository.getWeatherForecast() }
    }

    @Test
    fun `invoke should wrap empty list in Result_success`() = runTest {
        every { repository.getWeatherForecast() } returns flowOf(emptyList<Weather>())

        useCase.invoke(null).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(emptyList<Weather>(), result.getOrNull())
            awaitComplete()
        }

        verify { repository.getWeatherForecast() }
    }

    @Test
    fun `invoke should catch exception from repository and emit Result_failure`() = runTest {
        val exception = RuntimeException("Repository error")

        every { repository.getWeatherForecast() } throws exception

        useCase.invoke(null).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            assertEquals("Repository error", result.exceptionOrNull()?.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit multiple results when repository emits multiple lists`() = runTest {
        val firstList = listOf(
            createWeather(id = "1_0", tempDay = 20.0)
        )
        val secondList = listOf(
            createWeather(id = "1_0", tempDay = 20.0),
            createWeather(id = "2_1", tempDay = 18.0)
        )

        every { repository.getWeatherForecast() } returns flowOf(firstList, secondList)

        useCase.invoke(null).test {
            val firstResult = awaitItem()
            assertTrue(firstResult.isSuccess)
            assertEquals(firstList, firstResult.getOrNull())

            val secondResult = awaitItem()
            assertTrue(secondResult.isSuccess)
            assertEquals(secondList, secondResult.getOrNull())

            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle IllegalArgumentException from repository`() = runTest {
        val exception = IllegalArgumentException("Invalid parameters")

        every { repository.getWeatherForecast() } throws exception

        useCase.invoke(null).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            val thrownException = result.exceptionOrNull()
            if (thrownException !is IllegalArgumentException) {
                fail("Expected IllegalArgumentException but got ${thrownException?.javaClass?.simpleName}")
            }
            awaitComplete()
        }
    }

    @Test
    fun `invoke should wrap IOException in Result_failure`() = runTest {
        val exception = java.io.IOException("Network error")

        every { repository.getWeatherForecast() } throws exception

        useCase.invoke(null).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            val thrownException = result.exceptionOrNull()
            if (thrownException !is java.io.IOException) {
                fail("Expected IOException but got ${thrownException?.javaClass?.simpleName}")
            }
            assertEquals("Network error", thrownException?.message.orEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle empty flow from repository`() = runTest {
        every { repository.getWeatherForecast() } returns flowOf()

        useCase.invoke(null).test {
            awaitComplete()
        }
    }

    @Test
    fun `invoke should emit success for each list emitted by repository`() = runTest {
        val list1 = listOf(createWeather(id = "1", tempDay = 15.0))
        val list2 = listOf(
            createWeather(id = "1", tempDay = 15.0),
            createWeather(id = "2", tempDay = 18.0)
        )
        val list3 = listOf(
            createWeather(id = "1", tempDay = 15.0),
            createWeather(id = "2", tempDay = 18.0),
            createWeather(id = "3", tempDay = 22.0)
        )

        every { repository.getWeatherForecast() } returns flowOf(list1, list2, list3)

        useCase.invoke(null).test {
            repeat(3) {
                val result = awaitItem()
                assertTrue(result.isSuccess)
                assertTrue((result.getOrNull()?.size ?: 0) > 0)
            }
            awaitComplete()
        }
    }

    @Test
    fun `invoke should properly convert exception message in Result_failure`() = runTest {
        val errorMessage = "API rate limit exceeded"
        val exception = RuntimeException(errorMessage)

        every { repository.getWeatherForecast() } throws exception

        useCase.invoke(null).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            val thrownException = result.exceptionOrNull()
            assertEquals(errorMessage, thrownException?.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should call repository getWeatherForecast`() = runTest {
        every { repository.getWeatherForecast() } returns flowOf(emptyList())

        useCase.invoke(null).test {
            awaitItem()
            awaitComplete()
        }

        verify { repository.getWeatherForecast() }
    }

    @Test
    fun `invoke should wrap large weather list in Result_success`() = runTest {
        val largeList = (0..50).map { index ->
            createWeather(id = "${index}_${index}", tempDay = 15.0 + index)
        }

        every { repository.getWeatherForecast() } returns flowOf(largeList)

        useCase.invoke(null).test {
            val result = awaitItem()
            assertTrue(result.isSuccess)
            assertEquals(51, result.getOrNull()?.size)
            awaitComplete()
        }
    }

    @Test
    fun `invoke should handle NullPointerException from repository`() = runTest {
        val exception = NullPointerException("Null response from API")

        every { repository.getWeatherForecast() } throws exception

        useCase.invoke(null).test {
            val result = awaitItem()
            assertTrue(result.isFailure)
            val thrownException = result.exceptionOrNull()
            if (thrownException !is NullPointerException) {
                fail("Expected NullPointerException but got ${thrownException?.javaClass?.simpleName}")
            }
            assertEquals("Null response from API", thrownException?.message.orEmpty())
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