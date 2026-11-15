package com.example.data.remote

import com.squareup.moshi.Json
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query

data class OpenMeteoForecastResponse(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val current: CurrentWeatherDto? = null,
    val hourly: HourlyWeatherDto? = null,
    val daily: DailyWeatherDto? = null,
)

data class CurrentWeatherDto(
    val time: String = "",
    @param:Json(name = "temperature_2m") val temperature2m: Double = 0.0,
    @param:Json(name = "relative_humidity_2m") val relativeHumidity2m: Int = 0,
    @param:Json(name = "apparent_temperature") val apparentTemperature: Double = 0.0,
    @param:Json(name = "is_day") val isDay: Int = 1,
    val precipitation: Double = 0.0,
    @param:Json(name = "weather_code") val weatherCode: Int = 0,
    @param:Json(name = "cloud_cover") val cloudCover: Int = 0,
    @param:Json(name = "surface_pressure") val surfacePressure: Double = 1013.0,
    @param:Json(name = "wind_speed_10m") val windSpeed10m: Double = 0.0,
    @param:Json(name = "wind_direction_10m") val windDirection10m: Int = 0,
)

data class HourlyWeatherDto(
    val time: List<String> = emptyList(),
    @param:Json(name = "temperature_2m") val temperature2m: List<Double> = emptyList(),
    @param:Json(name = "weather_code") val weatherCode: List<Int> = emptyList(),
    @param:Json(name = "precipitation_probability") val precipitationProbability: List<Int?> = emptyList(),
    @param:Json(name = "is_day") val isDay: List<Int> = emptyList(),
)

data class DailyWeatherDto(
    val time: List<String> = emptyList(),
    @param:Json(name = "weather_code") val weatherCode: List<Int> = emptyList(),
    @param:Json(name = "temperature_2m_max") val temperature2mMax: List<Double> = emptyList(),
    @param:Json(name = "temperature_2m_min") val temperature2mMin: List<Double> = emptyList(),
    val sunrise: List<String> = emptyList(),
    val sunset: List<String> = emptyList(),
    @param:Json(name = "uv_index_max") val uvIndexMax: List<Double?> = emptyList(),
    @param:Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int?> = emptyList(),
    @param:Json(name = "wind_speed_10m_max") val windSpeed10mMax: List<Double?> = emptyList(),
)

data class AirQualityResponse(
    val current: CurrentAqiDto? = null,
)

data class CurrentAqiDto(
    @param:Json(name = "us_aqi") val usAqi: Int? = null,
    @param:Json(name = "pm2_5") val pm25: Double? = null,
    val pm10: Double? = null,
)

data class GeocodingResponse(
    val results: List<GeocodingResultDto>? = null,
)

data class GeocodingResultDto(
    val id: Long = 0L,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    @param:Json(name = "country_code") val countryCode: String? = null,
    val country: String? = null,
    val admin1: String? = null,
    val admin2: String? = null,
)

interface OpenMeteoForecastApi {
    @GET("v1/forecast")
    suspend fun getCityForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String =
            "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,cloud_cover,surface_pressure,wind_speed_10m,wind_direction_10m",
        @Query("hourly") hourly: String =
            "temperature_2m,weather_code,precipitation_probability,is_day",
        @Query("daily") daily: String =
            "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_probability_max,wind_speed_10m_max",
        @Query("timezone") timezone: String = "Asia/Kolkata",
        @Query("forecast_days") forecastDays: Int = 7,
    ): OpenMeteoForecastResponse

    @GET("v1/forecast")
    suspend fun getBatchCurrentWeatherRaw(
        @Query("latitude", encoded = true) latitudes: String,
        @Query("longitude", encoded = true) longitudes: String,
        @Query("current") current: String =
            "temperature_2m,relative_humidity_2m,is_day,weather_code,wind_speed_10m",
        @Query("timezone") timezone: String = "Asia/Kolkata",
    ): ResponseBody
}

interface OpenMeteoAirQualityApi {
    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "us_aqi,pm2_5,pm10",
        @Query("timezone") timezone: String = "Asia/Kolkata",
    ): AirQualityResponse
}

interface OpenMeteoGeocodingApi {
    @GET("v1/search")
    suspend fun searchIndiaLocations(
        @Query("name") query: String,
        @Query("count") count: Int = 20,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json",
    ): GeocodingResponse
}
