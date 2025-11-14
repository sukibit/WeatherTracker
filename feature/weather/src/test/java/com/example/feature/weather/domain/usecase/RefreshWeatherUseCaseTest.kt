package com.example.feature.weather.domain.usecase

import com.example.feature.weather.domain.model.RefreshWeatherParams
import com.example.feature.weather.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RefreshWeatherUseCaseTest {

    private lateinit var repository: WeatherRepository
    private lateinit var useCase: RefreshWeatherUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = RefreshWeatherUseCase(repository)
    }

    @Test
    fun `invoke should return Result_success when repository refreshes successfully`() = runTest {
        val params = RefreshWeatherParams(
            latitude = 40.4983,
            longitude = -3.5676,
            apiKey = "test_api_key"
        )

        coEvery {
            repository.refreshWeather(
                latitude = params.latitude,
                longitude = params.longitude,
                apiKey = params.apiKey
            )
        } just io.mockk.Runs

        val result = useCase.invoke(params)

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
        coVerify {
            repository.refreshWeather(
                latitude = 40.4983,
                longitude = -3.5676,
                apiKey = "test_api_key"
            )
        }
    }

    @Test
    fun `invoke should catch exception from repository and return Result_failure`() = runTest {
        val params = RefreshWeatherParams(
            latitude = 40.4983,
            longitude = -3.5676,
            apiKey = "test_api_key"
        )
        val exception = RuntimeException("API error")

        coEvery {
            repository.refreshWeather(
                latitude = params.latitude,
                longitude = params.longitude,
                apiKey = params.apiKey
            )
        } throws exception

        val result = useCase.invoke(params)

        assertTrue(result.isFailure)
        val thrownException = result.exceptionOrNull()
        if (thrownException !is RuntimeException) {
            fail("Expected RuntimeException but got ${thrownException?.javaClass?.simpleName}")
        }
        assertEquals("API error", thrownException?.message.orEmpty())
    }

    @Test
    fun `invoke should call repository with correct coordinates`() = runTest {
        val latitude = 40.4983
        val longitude = -3.5676
        val apiKey = "test_api_key"
        val params = RefreshWeatherParams(
            latitude = latitude,
            longitude = longitude,
            apiKey = apiKey
        )

        coEvery {
            repository.refreshWeather(latitude, longitude, apiKey)
        } just io.mockk.Runs

        useCase.invoke(params)

        coVerify {
            repository.refreshWeather(latitude, longitude, apiKey)
        }
    }

    @Test
    fun `invoke should handle IllegalArgumentException from repository`() = runTest {
        val params = RefreshWeatherParams(
            latitude = 40.4983,
            longitude = -3.5676,
            apiKey = "test_api_key"
        )
        val exception = IllegalArgumentException("Invalid coordinates")

        coEvery {
            repository.refreshWeather(any(), any(), any())
        } throws exception

        val result = useCase.invoke(params)

        assertTrue(result.isFailure)
        val thrownException = result.exceptionOrNull()
        if (thrownException !is IllegalArgumentException) {
            fail("Expected IllegalArgumentException but got ${thrownException?.javaClass?.simpleName}")
        }
    }

    @Test
    fun `invoke should wrap IOException in Result_failure`() = runTest {
        val params = RefreshWeatherParams(
            latitude = 40.4983,
            longitude = -3.5676,
            apiKey = "test_api_key"
        )
        val exception = java.io.IOException("Network error")

        coEvery {
            repository.refreshWeather(any(), any(), any())
        } throws exception

        val result = useCase.invoke(params)

        assertTrue(result.isFailure)
        val thrownException = result.exceptionOrNull()
        if (thrownException !is java.io.IOException) {
            fail("Expected IOException but got ${thrownException?.javaClass?.simpleName}")
        }
        assertEquals("Network error", thrownException?.message.orEmpty())
    }

    @Test
    fun `invoke should return success for valid RefreshWeatherParams`() = runTest {
        val params = RefreshWeatherParams(
            latitude = 51.5074,
            longitude = -0.1278,
            apiKey = "london_api_key"
        )

        coEvery {
            repository.refreshWeather(
                latitude = 51.5074,
                longitude = -0.1278,
                apiKey = "london_api_key"
            )
        } just io.mockk.Runs

        val result = useCase.invoke(params)

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `invoke should handle NullPointerException from repository`() = runTest {
        val params = RefreshWeatherParams(
            latitude = 40.4983,
            longitude = -3.5676,
            apiKey = "test_api_key"
        )
        val exception = NullPointerException("Null response from API")

        coEvery {
            repository.refreshWeather(any(), any(), any())
        } throws exception

        val result = useCase.invoke(params)

        assertTrue(result.isFailure)
        val thrownException = result.exceptionOrNull()
        if (thrownException !is NullPointerException) {
            fail("Expected NullPointerException but got ${thrownException?.javaClass?.simpleName}")
        }
        assertEquals("Null response from API", thrownException?.message.orEmpty())
    }

    @Test
    fun `invoke should preserve exception message in Result_failure`() = runTest {
        val params = RefreshWeatherParams(
            latitude = 40.4983,
            longitude = -3.5676,
            apiKey = "test_api_key"
        )
        val errorMessage = "API rate limit exceeded"
        val exception = RuntimeException(errorMessage)

        coEvery {
            repository.refreshWeather(any(), any(), any())
        } throws exception

        val result = useCase.invoke(params)

        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke should call refreshWeather with all params from RefreshWeatherParams`() = runTest {
        val params = RefreshWeatherParams(
            latitude = 35.6762,
            longitude = 139.6503,
            apiKey = "tokyo_key_12345"
        )

        coEvery {
            repository.refreshWeather(any(), any(), any())
        } just io.mockk.Runs

        useCase.invoke(params)

        coVerify(exactly = 1) {
            repository.refreshWeather(
                latitude = 35.6762,
                longitude = 139.6503,
                apiKey = "tokyo_key_12345"
            )
        }
    }

    @Test
    fun `invoke should return success with Unit as value`() = runTest {
        val params = RefreshWeatherParams(
            latitude = 40.4983,
            longitude = -3.5676,
            apiKey = "test_api_key"
        )

        coEvery {
            repository.refreshWeather(any(), any(), any())
        } just io.mockk.Runs

        val result = useCase.invoke(params)

        assertTrue(result.isSuccess)
        val value = result.getOrNull()
        assertEquals(Unit, value)
    }
}