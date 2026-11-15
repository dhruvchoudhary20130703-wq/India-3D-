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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddLocationAlt
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.BookmarkRemove
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.india.IndiaGeoData
import com.example.data.model.CitySummaryTemp
import com.example.data.model.IndianCity
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCondition
import com.example.data.model.formatTemp
import com.example.ui.components.Glass3DCard
import com.example.ui.components.WeatherEmblem3D
import com.example.ui.theme.MonsoonCyan
import com.example.ui.theme.SolarAmber

@Composable
fun CitiesCompareScreen(
    selectedCity: IndianCity,
    pinnedCities: List<IndianCity>,
    citySummaries: Map<String, CitySummaryTemp>,
    unit: TemperatureUnit,
    onSelectCityAndOpen3D: (IndianCity) -> Unit,
    onTogglePin: (IndianCity) -> Unit,
    onSetUnit: (TemperatureUnit) -> Unit,
    onOpenExplorer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spotlightTrio = listOf(
        IndiaGeoData.jodhpur,
        IndiaGeoData.barmer,
        IndiaGeoData.mumbai,
    )

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 760.dp)
                .align(Alignment.TopCenter)
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "India Cities & Compare",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                        )
                        Text(
                            text = "Jodhpur, Barmer, Mumbai & pinned skies",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MonsoonCyan,
                        )
                    }

                    SingleChoiceSegmentedButtonRow {
                        TemperatureUnit.entries.forEachIndexed { idx, itemUnit ->
                            SegmentedButton(
                                selected = unit == itemUnit,
                                onClick = { onSetUnit(itemUnit) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = idx,
                                    count = TemperatureUnit.entries.size,
                                ),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = SolarAmber,
                                    activeContentColor = Color(0xFF0A1328),
                                    inactiveContainerColor = Color.White.copy(alpha = 0.10f),
                                    inactiveContentColor = Color.White,
                                ),
                                modifier = Modifier.testTag("unit_segment_${itemUnit.name}"),
                            ) {
                                Text(
                                    text = itemUnit.symbol,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                }
            }

            // Side-by-side Trio Comparison: Jodhpur vs Barmer vs Mumbai
            item {
                Glass3DCard(modifier = Modifier.fillMaxWidth(), tiltX = 1.5f) {
                    Text(
                        text = "SPOTLIGHT PULSE · THAR TO KONKAN",
                        style = MaterialTheme.typography.labelLarge,
                        color = SolarAmber,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        spotlightTrio.forEach { city ->
                            val summary = citySummaries[city.id]
                            val isSel = city.id == selectedCity.id
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (isSel) SolarAmber.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (isSel) SolarAmber else Color.White.copy(alpha = 0.16f),
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { onSelectCityAndOpen3D(city) }
                                    .testTag("compare_trio_${city.id}"),
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    Text(
                                        text = city.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    Text(
                                        text = city.stateCode,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MonsoonCyan,
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    WeatherEmblem3D(
                                        condition = summary?.condition ?: WeatherCondition.CLEAR,
                                        isDay = summary?.isDay ?: true,
                                        modifier = Modifier.size(42.dp),
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = summary?.tempCelsius?.formatTemp(unit) ?: "--",
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Text(
                                        text = summary?.condition?.shortLabel ?: "Syncing",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.75f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                    if (summary != null) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "${summary.humidityPercent}% RH · ${summary.windSpeedKmh.toInt()} km/h",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.65f),
                                            maxLines = 1,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Pinned Cities List
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "PINNED CITIES (${pinnedCities.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = MonsoonCyan,
                    )
                    Button(
                        onClick = onOpenExplorer,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.14f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_indian_city_button"),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AddLocationAlt,
                            contentDescription = "Browse all India",
                            tint = SolarAmber,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Browse India",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                        )
                    }
                }
            }

            items(pinnedCities, key = { it.id }) { city ->
                val summary = citySummaries[city.id]
                val isSelected = city.id == selectedCity.id
                Glass3DCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onSelectCityAndOpen3D(city) }
                        .testTag("pinned_city_card_${city.id}"),
                    tiltX = 1.2f,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        WeatherEmblem3D(
                            condition = summary?.condition ?: WeatherCondition.CLEAR,
                            isDay = summary?.isDay ?: true,
                            modifier = Modifier.size(48.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = city.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = if (isSelected) SolarAmber else Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                if (isSelected) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = SolarAmber.copy(alpha = 0.22f),
                                    ) {
                                        Text(
                                            text = "ACTIVE 3D",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = SolarAmber,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${city.stateName} · ${city.biome.label}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.74f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (summary != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.WaterDrop,
                                            contentDescription = "Humidity",
                                            tint = MonsoonCyan,
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${summary.humidityPercent}%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.8f),
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Air,
                                            contentDescription = "Wind",
                                            tint = SolarAmber,
                                            modifier = Modifier.size(14.dp),
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${summary.windSpeedKmh.toInt()} km/h",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color.White.copy(alpha = 0.8f),
                                        )
                                    }
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = summary?.tempCelsius?.formatTemp(unit) ?: "--",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = summary?.condition?.shortLabel ?: "Tap to load",
                                style = MaterialTheme.typography.labelSmall,
                                color = MonsoonCyan,
                            )
                        }

                        if (pinnedCities.size > 1) {
                            IconButton(
                                onClick = { onTogglePin(city) },
                                modifier = Modifier.minimumInteractiveComponentSize(),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.BookmarkRemove,
                                    contentDescription = "Unpin ${city.name}",
                                    tint = Color.White.copy(alpha = 0.55f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
