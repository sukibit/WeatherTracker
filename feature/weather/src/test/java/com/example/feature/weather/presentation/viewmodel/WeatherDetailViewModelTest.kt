package com.example.feature.weather.presentation.viewmodel

import app.cash.turbine.test
import com.example.feature.weather.domain.model.Weather
import com.example.feature.weather.domain.usecase.GetWeatherDetailUseCase
import com.example.feature.weather.presentation.contract.WeatherDetailContract
import com.example.feature.weather.presentation.model.WeatherUi
import com.example.feature.weather.presentation.model.WeatherUiMapper
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WeatherDetailViewModelTest {

    private lateinit var getWeatherDetailUseCase: GetWeatherDetailUseCase
    private lateinit var viewModel: WeatherDetailViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getWeatherDetailUseCase = mockk()
        viewModel = WeatherDetailViewModel(
            getWeatherDetailUseCase = getWeatherDetailUseCase
        )
        mockkObject(WeatherUiMapper)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `createInitialState should return state with default values`() = runTest {
        val initialState = viewModel.createInitialState()
        assertEquals(WeatherDetailContract.State(), initialState)
    }

    @Test
    fun `handleEvent OnInit should load weather detail for given weatherId`() = runTest {
        val weatherId = "1_0"
        val weather = Weather(
            id = weatherId,
            date = 1699000000L,
            tempDay = 20.0,
            tempMin = 15.0,
            tempMax = 25.0,
            humidity = 65,
            windSpeed = 5.0,
            description = "sunny",
            icon = "01d"
        )
        val weatherUi = WeatherUi(
            id = weatherId,
            date = "Mon, 01 Nov",
            tempDay = "20.0°C",
            tempMin = "15.0°C",
            tempMax = "25.0°C",
            humidity = "65%",
            windSpeed = "5.0 m/s",
            description = "Sunny",
            iconUrl = "https://openweathermap.org/img/wn/01d@2x.png"
        )

        every { getWeatherDetailUseCase(weatherId) } returns flowOf(Result.success(weather))
        every { WeatherUiMapper.mapWeatherToUi(weather) } returns weatherUi
        viewModel.handleEvent(WeatherDetailContract.Event.OnInit(weatherId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertEquals(weatherUi, state.weather)
            assertFalse(state.showError)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleEvent OnInit should set error state on failure`() = runTest {
        val weatherId = "1_0"
        val errorMessage = "Network error"

        every { getWeatherDetailUseCase(weatherId) } returns flowOf(
            Result.failure(RuntimeException(errorMessage))
        )

        viewModel.handleEvent(WeatherDetailContract.Event.OnInit(weatherId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertTrue(state.showError)
            assertEquals(errorMessage, state.errorMessage)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleEvent OnInit should call usecase with correct weatherId`() = runTest {
        val weatherId = "test_id_12345"
        val weather = createWeather(id = weatherId)
        val weatherUi = createWeatherUi(id = weatherId)

        every { getWeatherDetailUseCase(weatherId) } returns flowOf(Result.success(weather))
        every { WeatherUiMapper.mapWeatherToUi(weather) } returns weatherUi

        viewModel.handleEvent(WeatherDetailContract.Event.OnInit(weatherId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(weatherUi.id, state.weather?.id)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleEvent OnErrorDismissed should clear error state`() = runTest {
        val weatherId = "1_0"
        every { getWeatherDetailUseCase(weatherId) } returns flowOf(
            Result.failure(RuntimeException("Error"))
        )

        viewModel.handleEvent(WeatherDetailContract.Event.OnInit(weatherId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleEvent(WeatherDetailContract.Event.OnErrorDismissed)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.showError)
            assertEquals("", state.errorMessage)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleEvent OnBackClicked should emit NavigateBack effect`() = runTest {
        viewModel.handleEvent(WeatherDetailContract.Event.OnBackClicked)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            val effect = awaitItem()
            if (effect !is WeatherDetailContract.Effect.NavigateBack) {
                fail("Expected NavigateBack effect but got ${effect?.javaClass?.simpleName}")
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `state should emit initial state`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(WeatherDetailContract.State(), initialState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `loadWeatherDetail should map weather to UI correctly`() = runTest {
        val weatherId = "1_0"
        val weather = Weather(
            id = weatherId,
            date = 1699000000L,
            tempDay = 20.5,
            tempMin = 15.5,
            tempMax = 25.5,
            humidity = 70,
            windSpeed = 6.5,
            description = "partly cloudy",
            icon = "02d"
        )
        val weatherUi = WeatherUi(
            id = weatherId,
            date = "Mon, 01 Nov",
            tempDay = "20.5°C",
            tempMin = "15.5°C",
            tempMax = "25.5°C",
            humidity = "70%",
            windSpeed = "6.5 m/s",
            description = "Partly cloudy",
            iconUrl = "https://openweathermap.org/img/wn/02d@2x.png"
        )

        every { getWeatherDetailUseCase(weatherId) } returns flowOf(Result.success(weather))
        every { WeatherUiMapper.mapWeatherToUi(weather) } returns weatherUi

        viewModel.handleEvent(WeatherDetailContract.Event.OnInit(weatherId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(weatherUi, state.weather)
            assertEquals("20.5°C", state.weather?.tempDay)
            assertEquals("Partly cloudy", state.weather?.description)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `error state should persist until OnErrorDismissed is called`() = runTest {
        val weatherId = "1_0"
        val errorMessage = "Database error"

        every { getWeatherDetailUseCase(weatherId) } returns flowOf(
            Result.failure(RuntimeException(errorMessage))
        )

        viewModel.handleEvent(WeatherDetailContract.Event.OnInit(weatherId))
        testDispatcher.scheduler.advanceUntilIdle()

        var hasError = false
        viewModel.state.test {
            val state = awaitItem()
            hasError = state.showError
            cancelAndConsumeRemainingEvents()
        }

        assertTrue(hasError)

        viewModel.handleEvent(WeatherDetailContract.Event.OnErrorDismissed)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.showError)
            assertEquals("", state.errorMessage)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleEvent OnInit should handle NullPointerException from usecase`() = runTest {
        val weatherId = "1_0"
        val exception = NullPointerException("Null data from database")

        every { getWeatherDetailUseCase(weatherId) } returns flowOf(
            Result.failure(exception)
        )

        viewModel.handleEvent(WeatherDetailContract.Event.OnInit(weatherId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showError)
            assertEquals("Null data from database", state.errorMessage)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleEvent OnInit should handle IllegalArgumentException from usecase`() = runTest {
        val weatherId = "invalid_id"
        val exception = IllegalArgumentException("Invalid weather ID format")

        every { getWeatherDetailUseCase(weatherId) } returns flowOf(
            Result.failure(exception)
        )

        viewModel.handleEvent(WeatherDetailContract.Event.OnInit(weatherId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showError)
            assertEquals("Invalid weather ID format", state.errorMessage)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `SSOT principle - Detail screen shows data from Room`() = runTest {
        val weatherId = "1_0"
        val weather = createWeather(id = weatherId, tempDay = 22.0, description = "rainy")
        val weatherUi = createWeatherUi(id = weatherId, tempDay = "22.0°C", description = "Rainy")

        every { getWeatherDetailUseCase(weatherId) } returns flowOf(Result.success(weather))
        every { WeatherUiMapper.mapWeatherToUi(weather) } returns weatherUi

        viewModel.handleEvent(WeatherDetailContract.Event.OnInit(weatherId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(weatherUi, state.weather)
            assertEquals("Rainy", state.weather?.description)
            assertEquals("22.0°C", state.weather?.tempDay)
            cancelAndConsumeRemainingEvents()
        }
    }

    private fun createWeather(
        id: String = "default",
        tempDay: Double = 20.0,
        description: String = "sunny"
    ): Weather {
        return Weather(
            id = id,
            date = 1699000000L,
            tempDay = tempDay,
            tempMin = tempDay - 5.0,
            tempMax = tempDay + 5.0,
            humidity = 65,
            windSpeed = 5.0,
            description = description,
            icon = "01d"
        )
    }

    private fun createWeatherUi(
        id: String = "default",
        tempDay: String = "20.0°C",
        description: String = "Sunny"
    ): WeatherUi {
        return WeatherUi(
            id = id,
            date = "Mon, 01 Nov",
            tempDay = tempDay,
            tempMin = "15.0°C",
            tempMax = "25.0°C",
            humidity = "65%",
            windSpeed = "5.0 m/s",
            description = description,
            iconUrl = "https://openweathermap.org/img/wn/01d@2x.png"
        )
    }
}