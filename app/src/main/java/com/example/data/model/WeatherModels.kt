package com.example.data.model

enum class BiomeType(val label: String) {
    THAR_DESERT("Thar Desert"),
    COASTAL("Coastal & Konkan"),
    HIMALAYAN("Himalayan Peaks"),
    PLAINS("Alluvial Plains"),
    DECCAN_PLATEAU("Deccan Plateau"),
    NORTHEAST_HILLS("Eastern Hills"),
    ISLANDS("Island Archipelago"),
}

enum class IndiaRegion(val label: String) {
    ALL("All India"),
    WEST_THAR("West & Thar"),
    NORTH_HIMALAYA("North & Himalayas"),
    CENTRAL_PLAINS("Central & Gangetic"),
    SOUTH_PENINSULA("South Peninsula"),
    EAST("East & Delta"),
    NORTHEAST("Northeast"),
    ISLANDS("Islands"),
}

enum class WeatherCondition(
    val displayName: String,
    val shortLabel: String,
) {
    CLEAR("Clear Sky", "Clear"),
    PARTLY_CLOUDY("Partly Cloudy", "Partly Cloudy"),
    CLOUDY("Overcast Clouds", "Cloudy"),
    FOG_HAZE("Fog & Haze", "Haze"),
    DRIZZLE("Light Drizzle", "Drizzle"),
    RAIN("Monsoon Rain", "Rain"),
    HEAVY_RAIN("Heavy Downpour", "Heavy Rain"),
    SNOW("Himalayan Snow", "Snow"),
    THUNDERSTORM("Thunderstorm", "Storm");

    companion object {
        fun fromWmoCode(code: Int): WeatherCondition = when (code) {
            0, 1 -> CLEAR
            2 -> PARTLY_CLOUDY
            3 -> CLOUDY
            45, 48 -> FOG_HAZE
            51, 53, 55, 56, 57 -> DRIZZLE
            61, 63, 80, 81 -> RAIN
            65, 66, 67, 82 -> HEAVY_RAIN
            71, 73, 75, 77, 85, 86 -> SNOW
            95, 96, 99 -> THUNDERSTORM
            else -> PARTLY_CLOUDY
        }
    }
}

enum class TemperatureUnit(val symbol: String) {
    CELSIUS("°C"),
    FAHRENHEIT("°F"),
}

data class IndianCity(
    val id: String,
    val name: String,
    val stateName: String,
    val stateCode: String,
    val lat: Double,
    val lon: Double,
    val biome: BiomeType,
    val region: IndiaRegion,
    val subtitle: String = "",
    val isSpotlight: Boolean = false,
)

data class IndianStateInfo(
    val code: String,
    val name: String,
    val isUnionTerritory: Boolean,
    val region: IndiaRegion,
    val biome: BiomeType,
    val capital: String,
    val centerLat: Double,
    val centerLon: Double,
    val cities: List<IndianCity>,
)

data class HourlyForecast(
    val timeIso: String,
    val hourLabel: String,
    val tempCelsius: Double,
    val condition: WeatherCondition,
    val precipitationProb: Int,
    val isDay: Boolean,
)

data class DailyForecast(
    val dateIso: String,
    val dayLabel: String,
    val condition: WeatherCondition,
    val tempMaxCelsius: Double,
    val tempMinCelsius: Double,
    val precipitationProbMax: Int,
    val uvIndexMax: Double,
    val windSpeedMaxKmh: Double,
    val sunriseIso: String,
    val sunsetIso: String,
)

data class AirQualityInfo(
    val usAqi: Int,
    val pm25: Double,
    val pm10: Double,
) {
    val categoryLabel: String
        get() = when {
            usAqi <= 50 -> "Good"
            usAqi <= 100 -> "Moderate"
            usAqi <= 150 -> "Sensitive"
            usAqi <= 200 -> "Unhealthy"
            else -> "Severe"
        }
}

data class CityWeather(
    val city: IndianCity,
    val tempCelsius: Double,
    val feelsLikeCelsius: Double,
    val humidityPercent: Int,
    val windSpeedKmh: Double,
    val windDirectionDeg: Int,
    val pressureHpa: Double,
    val cloudCoverPercent: Int,
    val precipitationMm: Double,
    val isDay: Boolean,
    val condition: WeatherCondition,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val airQuality: AirQualityInfo?,
    val fetchedAtMillis: Long = System.currentTimeMillis(),
) {
    val todayHighCelsius: Double
        get() = daily.firstOrNull()?.tempMaxCelsius ?: tempCelsius

    val todayLowCelsius: Double
        get() = daily.firstOrNull()?.tempMinCelsius ?: tempCelsius

    val todayUvMax: Double
        get() = daily.firstOrNull()?.uvIndexMax ?: 0.0

    val sunriseTime: String
        get() = daily.firstOrNull()?.sunriseIso?.substringAfter("T")?.take(5) ?: "06:15"

    val sunsetTime: String
        get() = daily.firstOrNull()?.sunsetIso?.substringAfter("T")?.take(5) ?: "18:30"
}

data class CitySummaryTemp(
    val cityId: String,
    val tempCelsius: Double,
    val condition: WeatherCondition,
    val isDay: Boolean,
    val windSpeedKmh: Double,
    val humidityPercent: Int,
)

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val weather: CityWeather) : WeatherUiState
    data class Error(val message: String, val city: IndianCity) : WeatherUiState
}

fun Double.formatTemp(unit: TemperatureUnit): String {
    val value = if (unit == TemperatureUnit.CELSIUS) this else (this * 9.0 / 5.0) + 32.0
    return "${kotlin.math.round(value).toInt()}°"
}

fun Double.toDisplayTempInt(unit: TemperatureUnit): Int {
    val value = if (unit == TemperatureUnit.CELSIUS) this else (this * 9.0 / 5.0) + 32.0
    return kotlin.math.round(value).toInt()
}

fun compassDirection(degrees: Int): String {
    val dirs = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val index = (((degrees % 360) + 360) % 360 + 22) / 45 % 8
    return dirs[index]
}
