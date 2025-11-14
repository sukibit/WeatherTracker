package com.example.feature.weather.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import coil3.compose.AsyncImage
import com.example.core.ui.Dimens
import com.example.feature.weather.R
import com.example.feature.weather.presentation.model.WeatherUi

@Composable
fun WeatherCard(
    weather: WeatherUi,
    onClick: () -> Unit,
    iconContent: @Composable () -> Unit = {
        AsyncImage(
            model = weather.iconUrl,
            contentDescription = stringResource(R.string.weather_icon_description),
            modifier = Modifier.size(Dimens.IconLarge),
            contentScale = ContentScale.Fit
        )
    }
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusMedium))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .border(
                width = Dimens.BorderThin,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(Dimens.RadiusMedium)
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onClick() })
            }
            .padding(Dimens.PaddingMedium)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = weather.date,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            iconContent()
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = weather.tempDay,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceXSmall))

                Text(
                    text = weather.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(Dimens.SpaceSmall))

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${stringResource(R.string.temperature_min_label)}: ${weather.tempMin}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.SpaceXXSmall))

                Text(
                    text = "${stringResource(R.string.temperature_max_label)}: ${weather.tempMax}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.SpaceSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherInfoChip(
                label = stringResource(R.string.humidity_label),
                value = weather.humidity,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(Dimens.SpaceSmall))
            WeatherInfoChip(
                label = stringResource(R.string.wind_label),
                value = weather.windSpeed,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun WeatherInfoChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Dimens.RadiusSmall))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .padding(Dimens.PaddingXSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Dimens.SpaceXSmall))

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}