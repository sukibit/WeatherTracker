package com.example.feature.weather.presentation.viewmodel

import app.cash.turbine.test
import com.example.core.data.WeatherConfig
import com.example.feature.weather.domain.model.RefreshWeatherParams
import com.example.feature.weather.domain.model.Weather
import com.example.feature.weather.domain.usecase.GetWeatherForecastUseCase
import com.example.feature.weather.domain.usecase.RefreshWeatherUseCase
import com.example.feature.weather.presentation.contract.WeatherListContract
import com.example.feature.weather.presentation.model.WeatherUi
import com.example.feature.weather.presentation.model.WeatherUiMapper
import io.mockk.coEvery
import io.mockk.coVerify
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
class WeatherListViewModelTest {

    private lateinit var getWeatherForecastUseCase: GetWeatherForecastUseCase
    private lateinit var refreshWeatherUseCase: RefreshWeatherUseCase
    private lateinit var viewModel: WeatherListViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getWeatherForecastUseCase = mockk()
        refreshWeatherUseCase = mockk()
        viewModel = WeatherListViewModel(
            getWeatherForecastUseCase = getWeatherForecastUseCase,
            refreshWeatherUseCase = refreshWeatherUseCase
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

        assertEquals(WeatherListContract.State(), initialState)
    }

    @Test
    fun `handleEvent OnInit should call refreshWeather with Madrid coordinates`() = runTest {
        every { getWeatherForecastUseCase() } returns flowOf(Result.success(emptyList()))
        coEvery { refreshWeatherUseCase(any()) } returns Result.success(Unit)

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            refreshWeatherUseCase(
                RefreshWeatherParams(
                    latitude = 40.4983,
                    longitude = -3.5676,
                    apiKey = WeatherConfig.apiKey
                )
            )
        }
    }

    @Test
    fun `handleEvent OnInit should observe weather forecast from Room (SSOT)`() = runTest {
        every { getWeatherForecastUseCase() } returns flowOf(Result.success(emptyList()))
        coEvery { refreshWeatherUseCase(any()) } returns Result.success(Unit)

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.showError)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleEvent OnInit should initialize only once`() = runTest {
        every { getWeatherForecastUseCase() } returns flowOf(Result.success(emptyList()))
        coEvery { refreshWeatherUseCase(any()) } returns Result.success(Unit)

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()
        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify(exactly = 1) {
            refreshWeatherUseCase(any())
        }
    }

    @Test
    fun `handleEvent OnWeatherClicked should emit NavigateToDetail effect`() = runTest {
        val weatherId = "1_0"

        viewModel.handleEvent(WeatherListContract.Event.OnWeatherClicked(weatherId))
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.effect.test {
            val effect = awaitItem()
            if (effect !is WeatherListContract.Effect.NavigateToDetail) {
                fail("Expected NavigateToDetail effect but got ${effect?.javaClass?.simpleName}")
            }
            assertEquals(weatherId, (effect as WeatherListContract.Effect.NavigateToDetail).weatherId)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `handleEvent OnErrorDismissed should clear error state`() = runTest {
        every { getWeatherForecastUseCase() } returns flowOf(Result.success(emptyList()))
        coEvery { refreshWeatherUseCase(any()) } returns Result.failure(RuntimeException("Test error"))

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.handleEvent(WeatherListContract.Event.OnErrorDismissed)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.showError)
            assertEquals("", state.errorMessage)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `observeWeatherData should update state with weather from Room on success`() = runTest {
        val mockWeatherList = emptyList<Weather>()
        val mockUiList = emptyList<WeatherUi>()

        every { getWeatherForecastUseCase() } returns flowOf(Result.success(mockWeatherList))
        every { WeatherUiMapper.mapWeatherListToUi(mockWeatherList) } returns mockUiList
        coEvery { refreshWeatherUseCase(any()) } returns Result.success(Unit)

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isLoading)
            assertFalse(state.showError)
            assertEquals(mockUiList, state.weather)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `observeWeatherData should set error state on failure from Room`() = runTest {
        val errorMessage = "Network error"
        every { getWeatherForecastUseCase() } returns flowOf(
            Result.failure(RuntimeException(errorMessage))
        )
        coEvery { refreshWeatherUseCase(any()) } returns Result.success(Unit)

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showError)
            assertEquals(errorMessage, state.errorMessage)
            assertFalse(state.isLoading)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `observeWeatherData should react to Room updates automatically (SSOT)`() = runTest {
        val weatherList = listOf(
            Weather(
                id = "1_0",
                date = 1699000000L,
                tempDay = 20.0,
                tempMin = 15.0,
                tempMax = 25.0,
                humidity = 65,
                windSpeed = 5.0,
                description = "sunny",
                icon = "01d"
            )
        )

        val uiList = listOf(
            WeatherUi(
                id = "1_0",
                date = "Mon, 01 Nov",
                tempDay = "20.0°C",
                tempMin = "15.0°C",
                tempMax = "25.0°C",
                humidity = "65%",
                windSpeed = "5.0 m/s",
                description = "Sunny",
                iconUrl = "https://openweathermap.org/img/wn/01d@2x.png"
            )
        )

        every { getWeatherForecastUseCase() } returns flowOf(Result.success(weatherList))
        every { WeatherUiMapper.mapWeatherListToUi(weatherList) } returns uiList
        coEvery { refreshWeatherUseCase(any()) } returns Result.success(Unit)

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(1, state.weather.size)
            assertEquals("Sunny", state.weather[0].description)
            assertFalse(state.isLoading)
            assertFalse(state.showError)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refreshWeather should set isRefreshing false on success`() = runTest {
        coEvery { refreshWeatherUseCase(any()) } returns Result.success(Unit)
        every { getWeatherForecastUseCase() } returns flowOf(Result.success(emptyList()))

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.isRefreshing)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refreshWeather should set error state on failure`() = runTest {
        val errorMessage = "API error"
        val exception = RuntimeException(errorMessage)
        coEvery { refreshWeatherUseCase(any()) } returns Result.failure(exception)
        every { getWeatherForecastUseCase() } returns flowOf(Result.success(emptyList()))

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showError)
            assertEquals(errorMessage, state.errorMessage)
            assertFalse(state.isRefreshing)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `refreshWeather should trigger Room update which UI observes (SSOT)`() = runTest {
        val weatherList = emptyList<Weather>()
        val uiList = emptyList<WeatherUi>()

        every { getWeatherForecastUseCase() } returns flowOf(Result.success(weatherList))
        every { WeatherUiMapper.mapWeatherListToUi(weatherList) } returns uiList
        coEvery { refreshWeatherUseCase(any()) } returns Result.success(Unit)

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify {
            refreshWeatherUseCase(
                RefreshWeatherParams(
                    latitude = 40.4983,
                    longitude = -3.5676,
                    apiKey = WeatherConfig.apiKey
                )
            )
        }
    }

    @Test
    fun `refreshWeather should handle NullPointerException`() = runTest {
        val exception = NullPointerException("API returned null")
        coEvery { refreshWeatherUseCase(any()) } returns Result.failure(exception)
        every { getWeatherForecastUseCase() } returns flowOf(Result.success(emptyList()))

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state.showError)
            assertEquals("API returned null", state.errorMessage)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `state should emit initial state`() = runTest {
        viewModel.state.test {
            val initialState = awaitItem()
            assertEquals(WeatherListContract.State(), initialState)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `error state should persist until OnErrorDismissed is called`() = runTest {
        coEvery { refreshWeatherUseCase(any()) } returns Result.failure(RuntimeException("Error"))
        every { getWeatherForecastUseCase() } returns flowOf(Result.success(emptyList()))

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        var hasError = false
        viewModel.state.test {
            val state = awaitItem()
            hasError = state.showError
            cancelAndConsumeRemainingEvents()
        }

        assertTrue(hasError)

        viewModel.handleEvent(WeatherListContract.Event.OnErrorDismissed)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertFalse(state.showError)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `SSOT principle - UI always reflects Room data via Flow`() = runTest {
        val weatherList = listOf(
            Weather(
                id = "1_0",
                date = 1699000000L,
                tempDay = 20.0,
                tempMin = 15.0,
                tempMax = 25.0,
                humidity = 65,
                windSpeed = 5.0,
                description = "sunny",
                icon = "01d"
            )
        )
        val uiList = listOf(
            WeatherUi(
                id = "1_0",
                date = "Mon, 01 Nov",
                tempDay = "20.0°C",
                tempMin = "15.0°C",
                tempMax = "25.0°C",
                humidity = "65%",
                windSpeed = "5.0 m/s",
                description = "Sunny",
                iconUrl = "https://openweathermap.org/img/wn/01d@2x.png"
            )
        )

        every { getWeatherForecastUseCase() } returns flowOf(Result.success(weatherList))
        every { WeatherUiMapper.mapWeatherListToUi(weatherList) } returns uiList
        coEvery { refreshWeatherUseCase(any()) } returns Result.success(Unit)

        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(uiList, state.weather)
            assertEquals(1, state.weather.size)
            assertEquals("Sunny", state.weather[0].description)
            cancelAndConsumeRemainingEvents()
        }
    }
}