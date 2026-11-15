package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.OpenInNew
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.data.india.IndiaGeoData
import com.example.data.model.CitySummaryTemp
import com.example.data.model.IndiaRegion
import com.example.data.model.IndianCity
import com.example.data.model.IndianStateInfo
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCondition
import com.example.data.model.formatTemp
import com.example.ui.components.Glass3DCard
import com.example.ui.components.India3DMapCanvas
import com.example.ui.components.IndianCitySearchBar
import com.example.ui.components.WeatherEmblem3D
import com.example.ui.theme.MonsoonCyan
import com.example.ui.theme.SolarAmber

@Composable
fun India3DExplorerScreen(
    selectedCity: IndianCity,
    selectedRegion: IndiaRegion,
    searchQuery: String,
    remoteSearchResults: List<IndianCity>,
    isSearchingRemote: Boolean,
    expandedStateCode: String?,
    citySummaries: Map<String, CitySummaryTemp>,
    pinnedCityIds: Set<String>,
    unit: TemperatureUnit,
    onSelectRegion: (IndiaRegion) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSubmitSearch: (String) -> Unit,
    onToggleStateExpanded: (IndianStateInfo) -> Unit,
    onSelectCityAndOpen3D: (IndianCity) -> Unit,
    onSelectCityOnMap: (IndianCity) -> Unit,
    onTogglePin: (IndianCity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val trimmedQuery = searchQuery.trim()

    val localMatchedCities = remember(trimmedQuery) {
        if (trimmedQuery.isEmpty()) {
            emptyList()
        } else {
            IndiaGeoData.allCities.filter { city ->
                city.name.contains(trimmedQuery, ignoreCase = true) ||
                    city.stateName.contains(trimmedQuery, ignoreCase = true) ||
                    city.subtitle.contains(trimmedQuery, ignoreCase = true)
            }
        }
    }

    val combinedSearchCities = remember(localMatchedCities, remoteSearchResults) {
        (localMatchedCities + remoteSearchResults).distinctBy {
            "${it.name.lowercase()}_${it.stateName.lowercase()}"
        }
    }

    val filteredStates = remember(selectedRegion, trimmedQuery) {
        IndiaGeoData.allStatesAndUTs.filter { state ->
            val matchesRegion = selectedRegion == IndiaRegion.ALL || state.region == selectedRegion
            val matchesQuery = trimmedQuery.isEmpty() ||
                state.name.contains(trimmedQuery, ignoreCase = true) ||
                state.capital.contains(trimmedQuery, ignoreCase = true) ||
                state.cities.any { it.name.contains(trimmedQuery, ignoreCase = true) }
            matchesRegion && matchesQuery
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isWideLayout = maxWidth >= 720.dp

        if (isWideLayout) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Left Pane: 3D India Map
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ExplorerHeader(
                        selectedCity = selectedCity,
                        summary = citySummaries[selectedCity.id],
                        unit = unit,
                        onOpenSelected3D = { onSelectCityAndOpen3D(selectedCity) },
                    )
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color(0xFF09142B).copy(alpha = 0.72f),
                        border = BorderStroke(1.dp, MonsoonCyan.copy(alpha = 0.28f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) {
                        India3DMapCanvas(
                            selectedCity = selectedCity,
                            citySummaries = citySummaries,
                            unit = unit,
                            onSelectCity = onSelectCityOnMap,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                // Right Pane: Search + 36 States & Cities
                ExplorerDirectoryList(
                    searchQuery = searchQuery,
                    trimmedQuery = trimmedQuery,
                    selectedRegion = selectedRegion,
                    filteredStates = filteredStates,
                    combinedSearchCities = combinedSearchCities,
                    isSearchingRemote = isSearchingRemote,
                    expandedStateCode = expandedStateCode,
                    selectedCity = selectedCity,
                    citySummaries = citySummaries,
                    pinnedCityIds = pinnedCityIds,
                    unit = unit,
                    includeMapItem = false,
                    onSelectRegion = onSelectRegion,
                    onSearchQueryChange = onSearchQueryChange,
                    onSubmitSearch = onSubmitSearch,
                    onToggleStateExpanded = onToggleStateExpanded,
                    onSelectCityAndOpen3D = onSelectCityAndOpen3D,
                    onSelectCityOnMap = onSelectCityOnMap,
                    onTogglePin = onTogglePin,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        } else {
            ExplorerDirectoryList(
                searchQuery = searchQuery,
                trimmedQuery = trimmedQuery,
                selectedRegion = selectedRegion,
                filteredStates = filteredStates,
                combinedSearchCities = combinedSearchCities,
                isSearchingRemote = isSearchingRemote,
                expandedStateCode = expandedStateCode,
                selectedCity = selectedCity,
                citySummaries = citySummaries,
                pinnedCityIds = pinnedCityIds,
                unit = unit,
                includeMapItem = true,
                onSelectRegion = onSelectRegion,
                onSearchQueryChange = onSearchQueryChange,
                onSubmitSearch = onSubmitSearch,
                onToggleStateExpanded = onToggleStateExpanded,
                onSelectCityAndOpen3D = onSelectCityAndOpen3D,
                onSelectCityOnMap = onSelectCityOnMap,
                onTogglePin = onTogglePin,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Composable
private fun ExplorerHeader(
    selectedCity: IndianCity,
    summary: CitySummaryTemp?,
    unit: TemperatureUnit,
    onOpenSelected3D: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "India 3D Atlas",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
            )
            Text(
                text = "28 States · 8 Union Territories · All Cities",
                style = MaterialTheme.typography.bodyMedium,
                color = MonsoonCyan,
            )
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = SolarAmber.copy(alpha = 0.22f),
            border = BorderStroke(1.dp, SolarAmber.copy(alpha = 0.65f)),
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onOpenSelected3D)
                .testTag("open_selected_sky3d"),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = buildString {
                        append(selectedCity.name)
                        if (summary != null) {
                            append(" ")
                            append(summary.tempCelsius.formatTemp(unit))
                        }
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.OpenInNew,
                    contentDescription = "View ${selectedCity.name} in 3D",
                    tint = SolarAmber,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun ExplorerDirectoryList(
    searchQuery: String,
    trimmedQuery: String,
    selectedRegion: IndiaRegion,
    filteredStates: List<IndianStateInfo>,
    combinedSearchCities: List<IndianCity>,
    isSearchingRemote: Boolean,
    expandedStateCode: String?,
    selectedCity: IndianCity,
    citySummaries: Map<String, CitySummaryTemp>,
    pinnedCityIds: Set<String>,
    unit: TemperatureUnit,
    includeMapItem: Boolean,
    onSelectRegion: (IndiaRegion) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSubmitSearch: (String) -> Unit,
    onToggleStateExpanded: (IndianStateInfo) -> Unit,
    onSelectCityAndOpen3D: (IndianCity) -> Unit,
    onSelectCityOnMap: (IndianCity) -> Unit,
    onTogglePin: (IndianCity) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (includeMapItem) {
            item {
                ExplorerHeader(
                    selectedCity = selectedCity,
                    summary = citySummaries[selectedCity.id],
                    unit = unit,
                    onOpenSelected3D = { onSelectCityAndOpen3D(selectedCity) },
                )
            }

            item {
                Surface(
                    shape = RoundedCornerShape(28.dp),
                    color = Color(0xFF09142B).copy(alpha = 0.75f),
                    border = BorderStroke(1.dp, MonsoonCyan.copy(alpha = 0.28f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp),
                ) {
                    India3DMapCanvas(
                        selectedCity = selectedCity,
                        citySummaries = citySummaries,
                        unit = unit,
                        onSelectCity = onSelectCityOnMap,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

        // Search Bar
        item {
            IndianCitySearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSubmitSearch = onSubmitSearch,
                suggestions = combinedSearchCities,
                citySummaries = citySummaries,
                isSearching = isSearchingRemote,
                unit = unit,
                onSelectCity = onSelectCityAndOpen3D,
                showDropdownSuggestions = false,
                testTagPrefix = "india_search",
            )
        }

        // Quick Spotlight Banner for Jodhpur, Barmer, Mumbai
        if (trimmedQuery.isEmpty()) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(IndiaGeoData.spotlightCities.take(6), key = { it.id }) { city ->
                        val summary = citySummaries[city.id]
                        val isSel = city.id == selectedCity.id
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (isSel) SolarAmber.copy(alpha = 0.28f) else Color.White.copy(alpha = 0.10f),
                            border = BorderStroke(
                                1.dp,
                                if (isSel) SolarAmber else Color.White.copy(alpha = 0.16f),
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable { onSelectCityAndOpen3D(city) }
                                .testTag("explorer_spotlight_${city.id}"),
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
                                        color = SolarAmber,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Region Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(IndiaRegion.entries) { region ->
                    val active = region == selectedRegion
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (active) MonsoonCyan else Color.White.copy(alpha = 0.08f),
                        border = BorderStroke(
                            1.dp,
                            if (active) MonsoonCyan else Color.White.copy(alpha = 0.18f),
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .clickable { onSelectRegion(region) }
                            .testTag("region_chip_${region.name}"),
                    ) {
                        Text(
                            text = region.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (active) Color(0xFF0A1328) else Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        )
                    }
                }
            }
        }

        // Live Search Results (local + Open-Meteo India geocoding)
        if (trimmedQuery.isNotEmpty()) {
            item {
                Text(
                    text = "MATCHING INDIAN LOCATIONS (${combinedSearchCities.size})",
                    style = MaterialTheme.typography.labelLarge,
                    color = MonsoonCyan,
                )
            }
            if (combinedSearchCities.isEmpty() && !isSearchingRemote) {
                item {
                    Glass3DCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "No matching Indian city found for \"$trimmedQuery\". Try Jodhpur, Barmer, Mumbai, Leh, or any town name.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.78f),
                        )
                    }
                }
            } else {
                items(combinedSearchCities, key = { "search_${it.id}_${it.lat}" }) { city ->
                    CityDirectoryRow(
                        city = city,
                        summary = citySummaries[city.id],
                        isSelected = city.id == selectedCity.id,
                        isPinned = pinnedCityIds.contains(city.id),
                        unit = unit,
                        onSelect = { onSelectCityAndOpen3D(city) },
                        onTogglePin = { onTogglePin(city) },
                    )
                }
            }
        }

        // All 28 States + 8 Union Territories Directory
        item {
            Text(
                text = "STATES & UNION TERRITORIES (${filteredStates.size})",
                style = MaterialTheme.typography.labelLarge,
                color = SolarAmber,
            )
        }

        items(filteredStates, key = { it.code }) { stateInfo ->
            val isExpanded = expandedStateCode == stateInfo.code || trimmedQuery.isNotEmpty()
            Glass3DCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("state_card_${stateInfo.code}"),
                tiltX = 1.0f,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onToggleStateExpanded(stateInfo) }
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stateInfo.name,
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (stateInfo.isUnionTerritory) {
                                    Color(0xFF818CF8).copy(alpha = 0.25f)
                                } else {
                                    MonsoonCyan.copy(alpha = 0.18f)
                                },
                            ) {
                                Text(
                                    text = if (stateInfo.isUnionTerritory) "UT" else stateInfo.code,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (stateInfo.isUnionTerritory) Color(0xFFC7D2FE) else MonsoonCyan,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Capital: ${stateInfo.capital} · ${stateInfo.cities.size} cities · ${stateInfo.biome.label}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.70f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Icon(
                        imageVector = if (isExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse ${stateInfo.name}" else "Expand ${stateInfo.name}",
                        tint = Color.White,
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier.padding(top = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        stateInfo.cities.forEach { city ->
                            CityDirectoryRow(
                                city = city,
                                summary = citySummaries[city.id],
                                isSelected = city.id == selectedCity.id,
                                isPinned = pinnedCityIds.contains(city.id),
                                unit = unit,
                                onSelect = { onSelectCityAndOpen3D(city) },
                                onTogglePin = { onTogglePin(city) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CityDirectoryRow(
    city: IndianCity,
    summary: CitySummaryTemp?,
    isSelected: Boolean,
    isPinned: Boolean,
    unit: TemperatureUnit,
    onSelect: () -> Unit,
    onTogglePin: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) SolarAmber.copy(alpha = 0.20f) else Color.White.copy(alpha = 0.07f),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) SolarAmber else Color.White.copy(alpha = 0.12f),
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onSelect)
            .testTag("city_row_${city.id}"),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WeatherEmblem3D(
                condition = summary?.condition ?: WeatherCondition.CLEAR,
                isDay = summary?.isDay ?: true,
                modifier = Modifier.size(34.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = if (city.subtitle.isNotBlank()) "${city.stateName} · ${city.subtitle}" else city.stateName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.70f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (summary != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = summary.tempCelsius.formatTemp(unit),
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = summary.condition.shortLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = MonsoonCyan,
                    )
                }
            } else {
                Text(
                    text = "View 3D",
                    style = MaterialTheme.typography.labelLarge,
                    color = MonsoonCyan,
                )
            }
            IconButton(
                onClick = onTogglePin,
                modifier = Modifier.minimumInteractiveComponentSize(),
            ) {
                Icon(
                    imageVector = if (isPinned) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                    contentDescription = "Pin ${city.name}",
                    tint = if (isPinned) SolarAmber else Color.White.copy(alpha = 0.65f),
                )
            }
        }
    }
}
