package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.CitySummaryTemp
import com.example.data.model.IndianCity
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCondition
import com.example.data.model.formatTemp
import com.example.ui.theme.MonsoonCyan
import com.example.ui.theme.SolarAmber

@Composable
fun IndianCitySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSubmitSearch: (String) -> Unit,
    suggestions: List<IndianCity>,
    citySummaries: Map<String, CitySummaryTemp>,
    isSearching: Boolean,
    unit: TemperatureUnit,
    onSelectCity: (IndianCity) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = stringResource(R.string.search_india_hint),
    showDropdownSuggestions: Boolean = true,
    testTagPrefix: String = "indian_city_search",
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var isFocused by remember { mutableStateOf(false) }

    val trimmedQuery = query.trim()
    val shouldShowDropdown = showDropdownSuggestions && isFocused && trimmedQuery.isNotEmpty()

    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                }
                .testTag("${testTagPrefix}_input"),
            placeholder = {
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.58f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = stringResource(R.string.search_city_icon_desc),
                    tint = if (isFocused) SolarAmber else MonsoonCyan,
                )
            },
            trailingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isSearching) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(18.dp),
                            color = MonsoonCyan,
                            strokeWidth = 2.dp,
                        )
                    }
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag("${testTagPrefix}_clear"),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Clear,
                                contentDescription = stringResource(R.string.clear_search),
                                tint = Color.White.copy(alpha = 0.78f),
                            )
                        }
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (trimmedQuery.isNotEmpty()) {
                        onSubmitSearch(trimmedQuery)
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                },
            ),
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = SolarAmber,
                focusedBorderColor = SolarAmber.copy(alpha = 0.85f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.24f),
                focusedContainerColor = Color(0xFF0E1D3C).copy(alpha = 0.84f),
                unfocusedContainerColor = Color(0xFF0E1D3C).copy(alpha = 0.62f),
            ),
        )

        AnimatedVisibility(
            visible = shouldShowDropdown,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically(),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .testTag("${testTagPrefix}_dropdown"),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFF0D1B38).copy(alpha = 0.96f),
                border = BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MonsoonCyan.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.14f),
                        ),
                    ),
                ),
                shadowElevation = 8.dp,
            ) {
                if (suggestions.isEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = null,
                            tint = SolarAmber,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isSearching) {
                                stringResource(R.string.searching_india_cities, trimmedQuery)
                            } else {
                                stringResource(R.string.no_india_city_match, trimmedQuery)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.82f),
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 6.dp),
                    ) {
                        val visibleSuggestions = suggestions.take(8)
                        visibleSuggestions.forEachIndexed { index, city ->
                            val summary = citySummaries[city.id]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable {
                                        onSelectCity(city)
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .testTag("${testTagPrefix}_result_${city.id}"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    WeatherEmblem3D(
                                        condition = summary?.condition ?: WeatherCondition.CLEAR,
                                        isDay = summary?.isDay ?: true,
                                        modifier = Modifier.size(32.dp),
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
                                            text = if (city.subtitle.isNotBlank()) {
                                                "${city.stateName} · ${city.subtitle}"
                                            } else {
                                                city.stateName
                                            },
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.White.copy(alpha = 0.68f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))
                                if (summary != null) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = summary.tempCelsius.formatTemp(unit),
                                            style = MaterialTheme.typography.titleLarge,
                                            color = SolarAmber,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = summary.condition.shortLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MonsoonCyan,
                                        )
                                    }
                                } else {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MonsoonCyan.copy(alpha = 0.16f),
                                    ) {
                                        Text(
                                            text = stringResource(R.string.fetch_weather_chip),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MonsoonCyan,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        )
                                    }
                                }
                            }
                            if (index < visibleSuggestions.lastIndex) {
                                HorizontalDivider(
                                    color = Color.White.copy(alpha = 0.08f),
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
