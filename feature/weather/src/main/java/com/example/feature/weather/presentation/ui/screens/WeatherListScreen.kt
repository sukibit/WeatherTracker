package com.example.feature.weather.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.core.ui.Dimens
import com.example.feature.weather.R
import com.example.feature.weather.presentation.contract.WeatherListContract
import com.example.feature.weather.presentation.ui.components.ErrorBannerInTopBar
import com.example.feature.weather.presentation.ui.components.LoadingView
import com.example.feature.weather.presentation.ui.components.WeatherCard
import com.example.feature.weather.presentation.viewmodel.WeatherListViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WeatherListScreen(
    navController: NavController,
    viewModel: WeatherListViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value
    val effect = viewModel.effect

    LaunchedEffect(Unit) {
        viewModel.handleEvent(WeatherListContract.Event.OnInit)
        effect.collectLatest { effect ->
            when (effect) {
                is WeatherListContract.Effect.NavigateToDetail -> {
                    navController.navigate("weather_detail/${effect.weatherId}")
                }
            }
        }
    }

    WeatherListContent(
        state = state,
        onEventSend = { event -> viewModel.handleEvent(event) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherListContent(
    state: WeatherListContract.State,
    onEventSend: (WeatherListContract.Event) -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(stringResource(R.string.weather_forecast_title)) }
                )
                if (state.showError) {
                    ErrorBannerInTopBar(
                        message = state.errorMessage,
                        onDismiss = { onEventSend(WeatherListContract.Event.OnErrorDismissed) }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading && state.weather.isEmpty() -> LoadingView()
                state.weather.isEmpty() -> EmptyStateWeatherListView()
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(Dimens.PaddingMedium),
                        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSmall)
                    ) {
                        items(state.weather.size) { index ->
                            WeatherCard(
                                weather = state.weather[index],
                                onClick = {
                                    onEventSend(
                                        WeatherListContract.Event.OnWeatherClicked(
                                            state.weather[index].id
                                        )
                                    )
                                }
                            )
                        }
                    }
                    if (state.isRefreshing) {
                        LoadingView()
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateWeatherListView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = stringResource(R.string.no_weather_data))
    }
}
