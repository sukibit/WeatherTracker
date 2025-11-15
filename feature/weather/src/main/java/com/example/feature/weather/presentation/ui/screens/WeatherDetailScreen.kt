package com.example.feature.weather.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.core.ui.Dimens
import com.example.feature.weather.R
import com.example.feature.weather.presentation.contract.WeatherDetailContract
import com.example.feature.weather.presentation.model.WeatherUi
import com.example.feature.weather.presentation.ui.components.DetailCard
import com.example.feature.weather.presentation.ui.components.ErrorBannerInTopBar
import com.example.feature.weather.presentation.ui.components.LoadingView
import com.example.feature.weather.presentation.viewmodel.WeatherDetailViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun WeatherDetailScreen(
    navController: NavController,
    weatherId: String,
    viewModel: WeatherDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsState().value
    val effect = viewModel.effect

    LaunchedEffect(Unit) {
        viewModel.handleEvent(WeatherDetailContract.Event.OnInit(weatherId))
        effect.collectLatest { effect ->
            when (effect) {
                is WeatherDetailContract.Effect.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }

    WeatherDetailContent(state = state, onEventSend = { event -> viewModel.handleEvent(event) })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherDetailContent(
    state: WeatherDetailContract.State, onEventSend: (WeatherDetailContract.Event) -> Unit
) {
    Scaffold(topBar = {
        Column {
            TopAppBar(title = { Text(stringResource(R.string.weather_details_title)) },
                navigationIcon = {
                    IconButton(onClick = {
                        onEventSend(WeatherDetailContract.Event.OnBackClicked)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_button_description)
                        )
                    }
                })
            if (state.showError) {
                ErrorBannerInTopBar(message = state.errorMessage,
                    onDismiss = { onEventSend(WeatherDetailContract.Event.OnErrorDismissed) })
            }
        }
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> LoadingView()
                state.weather == null -> EmptyStateWeatherDetailView()
                else -> WeatherDetailBody(state.weather)
            }
        }
    }
}

@Composable
fun WeatherDetailBody(
    weather: WeatherUi,
    iconContent: @Composable () -> Unit = {
        AsyncImage(
            model = weather.iconUrl,
            contentDescription = stringResource(R.string.weather_icon_description),
            modifier = Modifier.size(Dimens.IconXXLarge)
        )
    }
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.PaddingLarge),
        verticalArrangement = Arrangement.spacedBy(Dimens.SpaceLarge)
    ) {
        Text(
            text = weather.date,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        WeatherIconCard(weather = weather, iconContent = iconContent)

        DetailCard(
            title = stringResource(R.string.temperature_section_title),
            items = listOf(
                stringResource(R.string.temperature_day_label) to weather.tempDay,
                stringResource(R.string.temperature_min_label) to weather.tempMin,
                stringResource(R.string.temperature_max_label) to weather.tempMax
            )
        )

        DetailCard(
            title = stringResource(R.string.conditions_section_title),
            items = listOf(
                stringResource(R.string.humidity_label) to weather.humidity,
                stringResource(R.string.wind_label) to weather.windSpeed
            )
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceSmall))
    }
}

@Composable
fun WeatherIconCard(
    weather: WeatherUi,
    iconContent: @Composable () -> Unit = {
        if (weather.iconUrl.isNotEmpty()) {
            AsyncImage(
                model = weather.iconUrl,
                contentDescription = stringResource(R.string.weather_icon_description),
                modifier = Modifier.size(Dimens.IconXXLarge)
            )
        }
    }
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = Dimens.ElevationMedium,
                shape = RoundedCornerShape(Dimens.RadiusLarge),
                clip = false
            )
            .clip(RoundedCornerShape(Dimens.RadiusLarge))
            .background(MaterialTheme.colorScheme.surface)
            .padding(Dimens.PaddingXLarge)
    ) {
        iconContent()
        Spacer(modifier = Modifier.height(Dimens.SpaceMedium))
        Text(
            text = weather.tempDay,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceXSmall))

        Text(
            text = weather.description,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun EmptyStateWeatherDetailView() {
    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.weather_details_not_found))
    }
}