package com.example.feature.weather.presentation.ui.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import com.example.feature.weather.presentation.contract.WeatherDetailContract
import com.example.feature.weather.presentation.model.WeatherUi
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test

class WeatherDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun shouldRenderLoadingStateWithoutCrash() {
        val state = WeatherDetailContract.State(
            isLoading = true,
            weather = null
        )
        val onEventSend = mockk<(WeatherDetailContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherDetailContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }

        composeRule.waitForIdle()
    }

    @Test
    fun shouldDisplayEmptyStateWhenWeatherIsNull() {
        val state = WeatherDetailContract.State(
            isLoading = false,
            weather = null
        )
        val onEventSend = mockk<(WeatherDetailContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherDetailContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }
        composeRule.waitForIdle()
    }

    @Test
    fun shouldDisplayWeatherDetailsWhenDataAvailable() {
        val weather = WeatherUi(
            id = "1_0",
            date = "Mon, 01 Nov",
            tempDay = "22.5°C",
            tempMin = "17.5°C",
            tempMax = "27.5°C",
            humidity = "68%",
            windSpeed = "5.5 m/s",
            description = "Partly Cloudy",
            iconUrl = ""
        )
        val state = WeatherDetailContract.State(
            isLoading = false,
            weather = weather
        )
        val onEventSend = mockk<(WeatherDetailContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherDetailContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }

        composeRule.onNodeWithText("Mon, 01 Nov").assertIsDisplayed()
        composeRule.onAllNodesWithText("22.5°C").onFirst().assertIsDisplayed()
        composeRule.onNodeWithText("Partly Cloudy").assertIsDisplayed()
        composeRule.onNodeWithText("68%").assertIsDisplayed()
        composeRule.onNodeWithText("5.5 m/s").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayAllTemperatureValues() {
        val weather = WeatherUi(
            id = "1_0",
            date = "Wed, 03 Nov",
            tempDay = "20.0°C",
            tempMin = "15.0°C",
            tempMax = "25.0°C",
            humidity = "65%",
            windSpeed = "4.5 m/s",
            description = "Sunny",
            iconUrl = ""
        )
        val state = WeatherDetailContract.State(
            isLoading = false,
            weather = weather
        )
        val onEventSend = mockk<(WeatherDetailContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherDetailContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }
        composeRule.onAllNodesWithText("20.0°C").onFirst().assertIsDisplayed()
        composeRule.onAllNodesWithText("15.0°C").onFirst().assertIsDisplayed()
        composeRule.onAllNodesWithText("25.0°C").onFirst().assertIsDisplayed()
    }

    @Test
    fun shouldDisplayErrorBannerWhenShowErrorTrue() {
        val weather = WeatherUi(
            id = "1_0",
            date = "Thu, 04 Nov",
            tempDay = "18.0°C",
            tempMin = "13.0°C",
            tempMax = "23.0°C",
            humidity = "70%",
            windSpeed = "6.0 m/s",
            description = "Cloudy",
            iconUrl = ""
        )
        val state = WeatherDetailContract.State(
            isLoading = false,
            weather = weather,
            showError = true,
            errorMessage = "Failed to load details"
        )
        val onEventSend = mockk<(WeatherDetailContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherDetailContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }
        composeRule.onNodeWithText("Failed to load details").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayHumidityAndWindSpeed() {
        val weather = WeatherUi(
            id = "2_1",
            date = "Sat, 06 Nov",
            tempDay = "21.0°C",
            tempMin = "16.0°C",
            tempMax = "26.0°C",
            humidity = "60%",
            windSpeed = "3.5 m/s",
            description = "Clear",
            iconUrl = ""
        )
        val state = WeatherDetailContract.State(
            isLoading = false,
            weather = weather
        )
        val onEventSend = mockk<(WeatherDetailContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherDetailContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }

        composeRule.onNodeWithText("60%").assertIsDisplayed()
        composeRule.onNodeWithText("3.5 m/s").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayErrorBannerWhenConnectionError() {
        val weather = WeatherUi(
            id = "1_0",
            date = "Sun, 07 Nov",
            tempDay = "23.0°C",
            tempMin = "18.0°C",
            tempMax = "28.0°C",
            humidity = "55%",
            windSpeed = "2.5 m/s",
            description = "Windy",
            iconUrl = ""
        )
        val state = WeatherDetailContract.State(
            isLoading = false,
            weather = weather,
            showError = true,
            errorMessage = "Connection error"
        )
        val onEventSend = mockk<(WeatherDetailContract.Event) -> Unit>(relaxed = true)

        composeRule.setContent {
            MaterialTheme {
                Surface {
                    WeatherDetailContent(
                        state = state,
                        onEventSend = onEventSend
                    )
                }
            }
        }

        composeRule.onNodeWithText("Connection error").assertIsDisplayed()
    }
}