package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.CitySummaryTemp
import com.example.data.model.IndianCity
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCondition
import com.example.data.model.WeatherUiState
import com.example.data.model.formatTemp
import com.example.ui.components.AtmosphericMetricsGrid
import com.example.ui.components.DailyOutlookCard
import com.example.ui.components.Glass3DCard
import com.example.ui.components.HourlyForecastRibbon
import com.example.ui.components.IndianCitySearchBar
import com.example.ui.components.WeatherThreeJs3DView
import com.example.ui.theme.MonsoonCyan
import com.example.ui.theme.SolarAmber

@Composable
fun Sky3DScreen(
    selectedCity: IndianCity,
    weatherUiState: WeatherUiState,
    quickSwitchCities: List<IndianCity>,
    citySummaries: Map<String, CitySummaryTemp>,
    pinnedCityIds: Set<String>,
    unit: TemperatureUnit,
    searchQuery: String,
    searchSuggestions: List<IndianCity>,
    isSearchingRemote: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSubmitSearch: (String) -> Unit,
    onSelectSearchResult: (IndianCity) -> Unit,
    onSelectCity: (IndianCity) -> Unit,
    onTogglePin: (IndianCity) -> Unit,
    onRefresh: () -> Unit,
    onToggleUnit: () -> Unit,
    onOpenExplorer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isPinned = pinnedCityIds.contains(selectedCity.id)

    val selectedSummary = citySummaries[selectedCity.id]
    val activeCondition = when (weatherUiState) {
        is WeatherUiState.Success -> weatherUiState.weather.condition
        else -> selectedSummary?.condition ?: WeatherCondition.CLEAR
    }
    val activeIsDay = when (weatherUiState) {
        is WeatherUiState.Success -> weatherUiState.weather.isDay
        else -> selectedSummary?.isDay ?: true
    }
    val activeWind = when (weatherUiState) {
        is WeatherUiState.Success -> weatherUiState.weather.windSpeedKmh
        else -> selectedSummary?.windSpeedKmh ?: 10.0
    }
    val activeWindDir = when (weatherUiState) {
        is WeatherUiState.Success -> weatherUiState.weather.windDirectionDeg
        else -> 240
    }
    val activeCloudCover = when (weatherUiState) {
        is WeatherUiState.Success -> weatherUiState.weather.cloudCoverPercent
        else -> 25
    }
    val activeTempCelsius = when (weatherUiState) {
        is WeatherUiState.Success -> weatherUiState.weather.tempCelsius
        else -> selectedSummary?.tempCelsius ?: 28.0
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .align(Alignment.TopCenter)
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // 1. Header Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.LocationOn,
                                contentDescription = "Selected city",
                                tint = SolarAmber,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedCity.name,
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = buildString {
                                append(selectedCity.stateName)
                                append(" · ")
                                append(selectedCity.biome.label)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.78f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.14f),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable(onClick = onToggleUnit)
                                .minimumInteractiveComponentSize()
                                .testTag("unit_toggle_button"),
                        ) {
                            Text(
                                text = unit.symbol,
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            )
                        }
                        IconButton(
                            onClick = { onTogglePin(selectedCity) },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("pin_city_button"),
                        ) {
                            Icon(
                                imageVector = if (isPinned) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                                contentDescription = stringResource(R.string.toggle_pin_city),
                                tint = if (isPinned) SolarAmber else Color.White,
                            )
                        }
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("refresh_weather_button"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Refresh,
                                contentDescription = stringResource(R.string.refresh_weather),
                                tint = Color.White,
                            )
                        }
                    }
                }
            }

            // 2. Indian City Search Bar
            item {
                IndianCitySearchBar(
                    query = searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onSubmitSearch = onSubmitSearch,
                    suggestions = searchSuggestions,
                    citySummaries = citySummaries,
                    isSearching = isSearchingRemote,
                    unit = unit,
                    onSelectCity = onSelectSearchResult,
                    showDropdownSuggestions = true,
                    testTagPrefix = "sky_city_search",
                )
            }

            // 3. Quick-Switch India Spotlight Strip (Jodhpur, Barmer, Mumbai, etc.)
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(quickSwitchCities, key = { it.id }) { city ->
                        val isSelected = city.id == selectedCity.id
                        val summary = citySummaries[city.id]
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) {
                                SolarAmber.copy(alpha = 0.28f)
                            } else {
                                Color.White.copy(alpha = 0.10f)
                            },
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) SolarAmber else Color.White.copy(alpha = 0.18f),
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable { onSelectCity(city) }
                                .testTag("spotlight_chip_${city.id}"),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = city.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                )
                                if (summary != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = summary.tempCelsius.formatTemp(unit),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = if (isSelected) SolarAmber else MonsoonCyan,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                    item {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MonsoonCyan.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, MonsoonCyan.copy(alpha = 0.45f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable(onClick = onOpenExplorer)
                                .testTag("all_india_pill"),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Explore,
                                    contentDescription = "All India States",
                                    tint = MonsoonCyan,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "All 36 States & UTs",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }
            }

            // 3. Interactive Three.js 3D Weather Stage
            item {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color.Black.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.16f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                ) {
                    WeatherThreeJs3DView(
                        condition = activeCondition,
                        biome = selectedCity.biome,
                        isDay = activeIsDay,
                        windSpeedKmh = activeWind,
                        windDirectionDeg = activeWindDir,
                        cloudCoverPercent = activeCloudCover,
                        tempCelsius = activeTempCelsius,
                        cityName = selectedCity.name,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }

            // 4. Weather Data States
            when (weatherUiState) {
                is WeatherUiState.Loading -> {
                    item {
                        Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                CircularProgressIndicator(
                                    color = MonsoonCyan,
                                    modifier = Modifier.size(26.dp),
                                    strokeWidth = 2.8.dp,
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = stringResource(R.string.loading_weather),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                )
                            }
                        }
                    }
                }

                is WeatherUiState.Error -> {
                    item {
                        Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.CloudOff,
                                    contentDescription = "Connection error",
                                    tint = SolarAmber,
                                    modifier = Modifier.size(36.dp),
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Couldn't reach live weather for ${weatherUiState.city.name}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = weatherUiState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.72f),
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = onRefresh,
                                    colors = ButtonDefaults.buttonColors(containerColor = MonsoonCyan),
                                    modifier = Modifier.testTag("retry_weather_button"),
                                ) {
                                    Text(
                                        text = stringResource(R.string.retry),
                                        color = Color(0xFF0A1328),
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }
                }

                is WeatherUiState.Success -> {
                    val weather = weatherUiState.weather

                    // Hero Temperature & Summary Card
                    item {
                        Glass3DCard(modifier = Modifier.fillMaxWidth(), tiltX = 1.5f) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column {
                                    Text(
                                        text = weather.tempCelsius.formatTemp(unit),
                                        style = MaterialTheme.typography.displayLarge,
                                        color = Color.White,
                                    )
                                    Text(
                                        text = weather.condition.displayName,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = SolarAmber,
                                    )
                                    if (selectedCity.subtitle.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = selectedCity.subtitle,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.72f),
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = Color.White.copy(alpha = 0.10f),
                                    ) {
                                        Text(
                                            text = "H: ${weather.todayHighCelsius.formatTemp(unit)}  L: ${weather.todayLowCelsius.formatTemp(unit)}",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Feels like ${weather.feelsLikeCelsius.formatTemp(unit)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.85f),
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (weather.isDay) "Daylight · Live IST" else "Night Sky · Live IST",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MonsoonCyan,
                                    )
                                }
                            }
                        }
                    }

                    // Hourly Trajectory
                    item {
                        HourlyForecastRibbon(
                            hourly = weather.hourly,
                            unit = unit,
                        )
                    }

                    // Atmospheric 3D Metric Grid
                    item {
                        AtmosphericMetricsGrid(
                            weather = weather,
                            unit = unit,
                        )
                    }

                    // 7-Day Forecast
                    item {
                        DailyOutlookCard(
                            daily = weather.daily,
                            unit = unit,
                        )
                    }
                }
            }
        }
    }
}
