package com.example.feature.weather.presentation.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.feature.weather.presentation.contract.WeatherListContract
import com.example.feature.weather.presentation.model.WeatherUi
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test

class WeatherListScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun shouldRenderLoadingStateWithoutCrash() {
        val state = WeatherListContract.State(
            isLoading = true,
            weather = emptyList()
        )
        val onEventSend = mockk<(WeatherListContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherListContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }

        composeRule.waitForIdle()
    }

    @Test
    fun shouldRenderEmptyStateWithoutCrash() {
        val state = WeatherListContract.State(
            isLoading = false,
            weather = emptyList()
        )
        val onEventSend = mockk<(WeatherListContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherListContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }

        composeRule.waitForIdle()
    }

    @Test
    fun shouldDisplayWeatherListWhenDataAvailable() {
        val weatherList = listOf(
            WeatherUi(
                id = "1_0",
                date = "Mon, 01 Nov",
                tempDay = "20.0°C",
                tempMin = "15.0°C",
                tempMax = "25.0°C",
                humidity = "65%",
                windSpeed = "5.0 m/s",
                description = "Sunny",
                iconUrl = ""
            )
        )
        val state = WeatherListContract.State(
            isLoading = false,
            weather = weatherList
        )
        val onEventSend = mockk<(WeatherListContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherListContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }

        composeRule.onNodeWithText("Mon, 01 Nov").assertIsDisplayed()
        composeRule.onNodeWithText("20.0°C").assertIsDisplayed()
        composeRule.onNodeWithText("Sunny").assertIsDisplayed()
    }

    @Test
    fun shouldSendOnWeatherClickedEventWhenCardClicked() {
        val weatherList = listOf(
            WeatherUi(
                id = "1_0",
                date = "Mon, 01 Nov",
                tempDay = "20.0°C",
                tempMin = "15.0°C",
                tempMax = "25.0°C",
                humidity = "65%",
                windSpeed = "5.0 m/s",
                description = "Sunny",
                iconUrl = ""
            )
        )
        val state = WeatherListContract.State(
            isLoading = false,
            weather = weatherList
        )
        val onEventSend = mockk<(WeatherListContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherListContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }

        composeRule.onNodeWithText("20.0°C").performClick()

        verify {
            onEventSend(WeatherListContract.Event.OnWeatherClicked("1_0"))
        }
    }

    @Test
    fun shouldDisplayErrorBannerWhenShowErrorTrue() {
        val state = WeatherListContract.State(
            isLoading = false,
            weather = emptyList(),
            showError = true,
            errorMessage = "Network error occurred"
        )
        val onEventSend = mockk<(WeatherListContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherListContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }

        composeRule.onNodeWithText("Network error occurred").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayMultipleWeatherCards() {
        val weatherList = listOf(
            WeatherUi(
                id = "1_0",
                date = "Mon, 01 Nov",
                tempDay = "20.0°C",
                tempMin = "15.0°C",
                tempMax = "25.0°C",
                humidity = "65%",
                windSpeed = "5.0 m/s",
                description = "Sunny",
                iconUrl = ""
            ),
            WeatherUi(
                id = "2_1",
                date = "Tue, 02 Nov",
                tempDay = "18.0°C",
                tempMin = "13.0°C",
                tempMax = "23.0°C",
                humidity = "70%",
                windSpeed = "6.0 m/s",
                description = "Cloudy",
                iconUrl = ""
            )
        )
        val state = WeatherListContract.State(
            isLoading = false,
            weather = weatherList
        )
        val onEventSend = mockk<(WeatherListContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherListContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }

        composeRule.onNodeWithText("Mon, 01 Nov").assertIsDisplayed()
        composeRule.onNodeWithText("Tue, 02 Nov").assertIsDisplayed()
        composeRule.onNodeWithText("Sunny").assertIsDisplayed()
        composeRule.onNodeWithText("Cloudy").assertIsDisplayed()
    }
}