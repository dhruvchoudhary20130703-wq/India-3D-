package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.CompassCalibration
import androidx.compose.material.icons.rounded.Grain
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AirQualityInfo
import com.example.data.model.BiomeType
import com.example.data.model.CityWeather
import com.example.data.model.DailyForecast
import com.example.data.model.HourlyForecast
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCondition
import com.example.data.model.compassDirection
import com.example.data.model.formatTemp
import com.example.data.model.toDisplayTempInt
import com.example.ui.theme.AqiGood
import com.example.ui.theme.AqiModerate
import com.example.ui.theme.AqiPoor
import com.example.ui.theme.AqiSevere
import com.example.ui.theme.DesertCoral
import com.example.ui.theme.MonsoonCyan
import com.example.ui.theme.SolarAmber
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun atmosphereBackgroundColors(
    condition: WeatherCondition,
    biome: BiomeType,
    isDay: Boolean,
): List<Color> {
    if (!isDay) {
        return when (condition) {
            WeatherCondition.THUNDERSTORM, WeatherCondition.HEAVY_RAIN ->
                listOf(Color(0xFF060B19), Color(0xFF131D3B), Color(0xFF1D2447))
            else ->
                listOf(Color(0xFF070F24), Color(0xFF0E1D42), Color(0xFF162C5B))
        }
    }
    return when (condition) {
        WeatherCondition.THUNDERSTORM, WeatherCondition.HEAVY_RAIN ->
            listOf(Color(0xFF111C35), Color(0xFF1D325B), Color(0xFF284375))
        WeatherCondition.RAIN, WeatherCondition.DRIZZLE ->
            listOf(Color(0xFF0D2847), Color(0xFF164A7A), Color(0xFF1E6494))
        WeatherCondition.CLOUDY, WeatherCondition.FOG_HAZE ->
            listOf(Color(0xFF1C2D49), Color(0xFF2D4569), Color(0xFF3E5980))
        WeatherCondition.SNOW ->
            listOf(Color(0xFF172D4B), Color(0xFF274A73), Color(0xFF3B6B9B))
        WeatherCondition.CLEAR, WeatherCondition.PARTLY_CLOUDY -> {
            when (biome) {
                BiomeType.THAR_DESERT ->
                    listOf(Color(0xFF132954), Color(0xFF274D8A), Color(0xFF7B4B2A))
                BiomeType.COASTAL, BiomeType.ISLANDS ->
                    listOf(Color(0xFF092B5A), Color(0xFF0E5393), Color(0xFF1279B5))
                else ->
                    listOf(Color(0xFF0B2552), Color(0xFF144282), Color(0xFF1C62AD))
            }
        }
    }
}

@Composable
fun Glass3DCard(
    modifier: Modifier = Modifier,
    tiltX: Float = 2.0f,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.graphicsLayer {
            rotationX = tiltX
            cameraDistance = 14f * density
        },
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFF0F1E3D).copy(alpha = 0.62f),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.28f),
                    Color.White.copy(alpha = 0.06f),
                ),
            ),
        ),
        tonalElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = content,
        )
    }
}

@Composable
fun HourlyForecastRibbon(
    hourly: List<HourlyForecast>,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
) {
    Glass3DCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "24-HOUR TRAJECTORY",
                style = MaterialTheme.typography.labelLarge,
                color = MonsoonCyan,
            )
            Text(
                text = "IST · Live Curve",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.65f),
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (hourly.size >= 2) {
            HourlyTemperatureCurve(
                hourly = hourly.take(12),
                unit = unit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 2.dp),
        ) {
            items(hourly, key = { it.timeIso }) { item ->
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = if (item.hourLabel == "Now") {
                        Color.White.copy(alpha = 0.16f)
                    } else {
                        Color.White.copy(alpha = 0.06f)
                    },
                    border = if (item.hourLabel == "Now") {
                        BorderStroke(1.dp, MonsoonCyan.copy(alpha = 0.7f))
                    } else {
                        null
                    },
                ) {
                    Column(
                        modifier = Modifier
                            .width(68.dp)
                            .padding(vertical = 10.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = item.hourLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (item.hourLabel == "Now") MonsoonCyan else Color.White.copy(alpha = 0.78f),
                            fontWeight = FontWeight.SemiBold,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        WeatherEmblem3D(
                            condition = item.condition,
                            isDay = item.isDay,
                            modifier = Modifier.size(30.dp),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.tempCelsius.formatTemp(unit),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                        if (item.precipitationProb >= 15) {
                            Text(
                                text = "${item.precipitationProb}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MonsoonCyan,
                            )
                        } else {
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyTemperatureCurve(
    hourly: List<HourlyForecast>,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val temps = hourly.map { it.tempCelsius.toDisplayTempInt(unit).toFloat() }
        val minT = (temps.minOrNull() ?: 0f) - 1f
        val maxT = (temps.maxOrNull() ?: 1f) + 1f
        val span = (maxT - minT).coerceAtLeast(1f)
        val stepX = size.width / (temps.size - 1).coerceAtLeast(1)

        val points = temps.mapIndexed { idx, t ->
            val norm = (t - minT) / span
            Offset(
                x = idx * stepX,
                y = size.height - 10f - norm * (size.height - 20f),
            )
        }

        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                val p0 = points[i]
                val p1 = points[i + 1]
                val cx = (p0.x + p1.x) * 0.5f
                cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
            }
        }

        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(points.last().x, size.height)
            lineTo(points.first().x, size.height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(SolarAmber.copy(alpha = 0.28f), Color.Transparent),
            ),
        )
        drawPath(
            path = linePath,
            brush = Brush.horizontalGradient(colors = listOf(MonsoonCyan, SolarAmber)),
            style = Stroke(width = 3.2f, cap = StrokeCap.Round),
        )
        points.forEachIndexed { idx, pt ->
            if (idx == 0 || idx % 3 == 0) {
                drawCircle(color = SolarAmber, radius = 4.2f, center = pt)
            }
        }
    }
}

@Composable
fun DailyOutlookCard(
    daily: List<DailyForecast>,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
) {
    val weekMin = daily.minOfOrNull { it.tempMinCelsius } ?: 10.0
    val weekMax = daily.maxOfOrNull { it.tempMaxCelsius } ?: 40.0
    val weekSpan = (weekMax - weekMin).coerceAtLeast(1.0)

    Glass3DCard(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "7-DAY ATMOSPHERIC OUTLOOK",
            style = MaterialTheme.typography.labelLarge,
            color = SolarAmber,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            daily.forEach { day ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = day.dayLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        modifier = Modifier.width(62.dp),
                    )
                    WeatherEmblem3D(
                        condition = day.condition,
                        isDay = true,
                        modifier = Modifier.size(28.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (day.precipitationProbMax >= 15) "${day.precipitationProbMax}%" else "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MonsoonCyan,
                        modifier = Modifier.width(34.dp),
                    )
                    Text(
                        text = day.tempMinCelsius.formatTemp(unit),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.70f),
                        modifier = Modifier.width(36.dp),
                    )
                    // Thermal gradient bar
                    Canvas(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .padding(horizontal = 6.dp),
                    ) {
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.14f),
                            cornerRadius = CornerRadius(8f, 8f),
                        )
                        val startFrac = ((day.tempMinCelsius - weekMin) / weekSpan).toFloat().coerceIn(0f, 1f)
                        val endFrac = ((day.tempMaxCelsius - weekMin) / weekSpan).toFloat().coerceIn(startFrac + 0.08f, 1f)
                        val barLeft = size.width * startFrac
                        val barWidth = (size.width * (endFrac - startFrac)).coerceAtLeast(12f)
                        drawRoundRect(
                            brush = Brush.horizontalGradient(colors = listOf(MonsoonCyan, SolarAmber, DesertCoral)),
                            topLeft = Offset(barLeft, 0f),
                            size = Size(barWidth, size.height),
                            cornerRadius = CornerRadius(8f, 8f),
                        )
                    }
                    Text(
                        text = day.tempMaxCelsius.formatTemp(unit),
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(38.dp),
                    )
                }
            }
        }
    }
}

@Composable
fun AtmosphericMetricsGrid(
    weather: CityWeather,
    unit: TemperatureUnit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Metric3DTile(
                icon = Icons.Rounded.Air,
                label = "WIND",
                value = "${weather.windSpeedKmh.toInt()} km/h",
                subValue = "Direction ${compassDirection(weather.windDirectionDeg)} (${weather.windDirectionDeg}°)",
                accent = MonsoonCyan,
                modifier = Modifier.weight(1f),
            )
            Metric3DTile(
                icon = Icons.Rounded.WaterDrop,
                label = "HUMIDITY",
                value = "${weather.humidityPercent}%",
                subValue = "Feels like ${weather.feelsLikeCelsius.formatTemp(unit)}",
                accent = Color(0xFF60A5FA),
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Metric3DTile(
                icon = Icons.Rounded.WbSunny,
                label = "UV PEAK",
                value = String.format("%.1f", weather.todayUvMax),
                subValue = when {
                    weather.todayUvMax >= 8 -> "Very High · Sun protection"
                    weather.todayUvMax >= 5 -> "High · Shade midday"
                    else -> "Low to Moderate"
                },
                accent = SolarAmber,
                modifier = Modifier.weight(1f),
            )
            Metric3DTile(
                icon = Icons.Rounded.Speed,
                label = "PRESSURE",
                value = "${weather.pressureHpa.toInt()} hPa",
                subValue = "Cloud cover ${weather.cloudCoverPercent}%",
                accent = Color(0xFFA78BFA),
                modifier = Modifier.weight(1f),
            )
        }

        // Solar Arc + Air Quality row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SolarArcTile(
                sunrise = weather.sunriseTime,
                sunset = weather.sunsetTime,
                isDay = weather.isDay,
                modifier = Modifier.weight(1f),
            )
            AirQualityTile(
                aqi = weather.airQuality,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun Metric3DTile(
    icon: ImageVector,
    label: String,
    value: String,
    subValue: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    Glass3DCard(modifier = modifier, tiltX = 2.5f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = accent,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.70f),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subValue,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.72f),
            maxLines = 2,
        )
    }
}

@Composable
private fun SolarArcTile(
    sunrise: String,
    sunset: String,
    isDay: Boolean,
    modifier: Modifier = Modifier,
) {
    Glass3DCard(modifier = modifier, tiltX = 2.5f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.WbTwilight,
                contentDescription = "Sun path",
                tint = SolarAmber,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "SOLAR ARC",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.70f),
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            val w = size.width
            val h = size.height
            val arcPath = Path().apply {
                moveTo(8f, h - 8f)
                quadraticTo(w * 0.5f, -h * 0.4f, w - 8f, h - 8f)
            }
            drawLine(
                color = Color.White.copy(alpha = 0.22f),
                start = Offset(4f, h - 8f),
                end = Offset(w - 4f, h - 8f),
                strokeWidth = 2f,
            )
            drawPath(
                path = arcPath,
                color = SolarAmber.copy(alpha = 0.65f),
                style = Stroke(width = 3.2f, cap = StrokeCap.Round),
            )
            val t = if (isDay) 0.55f else 0.92f
            val orbX = (1 - t) * (1 - t) * 8f + 2 * (1 - t) * t * (w * 0.5f) + t * t * (w - 8f)
            val orbY = (1 - t) * (1 - t) * (h - 8f) + 2 * (1 - t) * t * (-h * 0.4f) + t * t * (h - 8f)
            drawCircle(
                color = if (isDay) SolarAmber else Color(0xFF93C5FD),
                radius = 7f,
                center = Offset(orbX, orbY),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = "↑ $sunrise", style = MaterialTheme.typography.labelSmall, color = Color.White)
            Text(text = "↓ $sunset", style = MaterialTheme.typography.labelSmall, color = Color.White)
        }
    }
}

@Composable
private fun AirQualityTile(
    aqi: AirQualityInfo?,
    modifier: Modifier = Modifier,
) {
    val aqiValue = aqi?.usAqi ?: 0
    val aqiColor = when {
        aqi == null -> MonsoonCyan
        aqiValue <= 50 -> AqiGood
        aqiValue <= 100 -> AqiModerate
        aqiValue <= 150 -> AqiPoor
        else -> AqiSevere
    }
    Glass3DCard(modifier = modifier, tiltX = 2.5f) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Grain,
                contentDescription = "Air Quality",
                tint = aqiColor,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "AIR QUALITY",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.70f),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (aqi != null) "${aqi.usAqi} AQI" else "--",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (aqi != null) "${aqi.categoryLabel} · PM2.5 ${aqi.pm25.toInt()}" else "Syncing AQI…",
            style = MaterialTheme.typography.bodyMedium,
            color = aqiColor,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
