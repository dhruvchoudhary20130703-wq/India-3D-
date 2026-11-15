package com.example.data.repository

import com.example.data.india.IndiaGeoData
import com.example.data.model.AirQualityInfo
import com.example.data.model.BiomeType
import com.example.data.model.CitySummaryTemp
import com.example.data.model.CityWeather
import com.example.data.model.DailyForecast
import com.example.data.model.HourlyForecast
import com.example.data.model.IndiaRegion
import com.example.data.model.IndianCity
import com.example.data.model.WeatherCondition
import com.example.data.remote.OpenMeteoAirQualityApi
import com.example.data.remote.OpenMeteoForecastApi
import com.example.data.remote.OpenMeteoForecastResponse
import com.example.data.remote.OpenMeteoGeocodingApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class WeatherRepository {

    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val forecastApi: OpenMeteoForecastApi = Retrofit.Builder()
        .baseUrl("https://api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(OpenMeteoForecastApi::class.java)

    private val airQualityApi: OpenMeteoAirQualityApi = Retrofit.Builder()
        .baseUrl("https://air-quality-api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(OpenMeteoAirQualityApi::class.java)

    private val geocodingApi: OpenMeteoGeocodingApi = Retrofit.Builder()
        .baseUrl("https://geocoding-api.open-meteo.com/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(OpenMeteoGeocodingApi::class.java)

    private val cityWeatherCache = ConcurrentHashMap<String, CityWeather>()
    private val customCities = ConcurrentHashMap<String, IndianCity>()
    private val cacheTtlMillis = 10 * 60 * 1000L

    fun getCityById(id: String): IndianCity? {
        return IndiaGeoData.findCityById(id) ?: customCities[id]
    }

    fun registerCustomCity(city: IndianCity) {
        customCities[city.id] = city
    }

    suspend fun getCityWeather(city: IndianCity, forceRefresh: Boolean = false): Result<CityWeather> {
        val cached = cityWeatherCache[city.id]
        if (!forceRefresh && cached != null && (System.currentTimeMillis() - cached.fetchedAtMillis) < cacheTtlMillis) {
            return Result.success(cached)
        }

        return withContext(Dispatchers.IO) {
            try {
                coroutineScope {
                    val forecastDeferred = async {
                        forecastApi.getCityForecast(latitude = city.lat, longitude = city.lon)
                    }
                    val aqiDeferred = async {
                        runCatching {
                            airQualityApi.getAirQuality(latitude = city.lat, longitude = city.lon)
                        }.getOrNull()
                    }

                    val forecast = forecastDeferred.await()
                    val aqiResp = aqiDeferred.await()
                    val mapped = mapToCityWeather(city, forecast, aqiResp?.current?.let { dto ->
                        dto.usAqi?.let { aqi ->
                            AirQualityInfo(
                                usAqi = aqi,
                                pm25 = dto.pm25 ?: 0.0,
                                pm10 = dto.pm10 ?: 0.0,
                            )
                        }
                    })
                    cityWeatherCache[city.id] = mapped
                    Result.success(mapped)
                }
            } catch (e: Exception) {
                if (cached != null) {
                    Result.success(cached)
                } else {
                    Result.failure(e)
                }
            }
        }
    }

    suspend fun fetchBatchSummaries(cities: List<IndianCity>): Map<String, CitySummaryTemp> {
        if (cities.isEmpty()) return emptyMap()
        return withContext(Dispatchers.IO) {
            val result = mutableMapOf<String, CitySummaryTemp>()
            // Chunk into batches of 40 to stay well within URL limits
            cities.chunked(40).forEach { chunk ->
                try {
                    val lats = chunk.joinToString(",") { String.format(Locale.US, "%.4f", it.lat) }
                    val lons = chunk.joinToString(",") { String.format(Locale.US, "%.4f", it.lon) }
                    val rawJson = forecastApi.getBatchCurrentWeatherRaw(lats, lons).string().trim()

                    val responses: List<OpenMeteoForecastResponse> = if (rawJson.startsWith("[")) {
                        val listType = Types.newParameterizedType(List::class.java, OpenMeteoForecastResponse::class.java)
                        val adapter = moshi.adapter<List<OpenMeteoForecastResponse>>(listType)
                        adapter.fromJson(rawJson) ?: emptyList()
                    } else {
                        val adapter = moshi.adapter(OpenMeteoForecastResponse::class.java)
                        listOfNotNull(adapter.fromJson(rawJson))
                    }

                    chunk.zip(responses).forEach { (city, resp) ->
                        val curr = resp.current
                        if (curr != null) {
                            result[city.id] = CitySummaryTemp(
                                cityId = city.id,
                                tempCelsius = curr.temperature2m,
                                condition = WeatherCondition.fromWmoCode(curr.weatherCode),
                                isDay = curr.isDay == 1,
                                windSpeedKmh = curr.windSpeed10m,
                                humidityPercent = curr.relativeHumidity2m,
                            )
                        }
                    }
                } catch (_: Exception) {
                    // Continue with remaining chunks gracefully
                }
            }
            result
        }
    }

    suspend fun searchIndianCitiesRemote(query: String): List<IndianCity> {
        if (query.trim().length < 2) return emptyList()
        return withContext(Dispatchers.IO) {
            try {
                val response = geocodingApi.searchIndiaLocations(query = query.trim())
                response.results.orEmpty()
                    .filter { it.countryCode.equals("IN", ignoreCase = true) }
                    .distinctBy { "${it.name.lowercase()}_${it.admin1?.lowercase().orEmpty()}" }
                    .map { dto ->
                        val stateName = dto.admin1 ?: "India"
                        val matchedState = IndiaGeoData.allStatesAndUTs.firstOrNull {
                            it.name.equals(stateName, ignoreCase = true) ||
                                stateName.contains(it.name, ignoreCase = true) ||
                                it.name.contains(stateName, ignoreCase = true)
                        }
                        val cityId = "geo_${dto.id}"
                        val city = IndianCity(
                            id = cityId,
                            name = dto.name,
                            stateName = matchedState?.name ?: stateName,
                            stateCode = matchedState?.code ?: "IN",
                            lat = dto.latitude,
                            lon = dto.longitude,
                            biome = matchedState?.biome ?: inferBiome(dto.latitude, dto.longitude),
                            region = matchedState?.region ?: IndiaRegion.ALL,
                            subtitle = listOfNotNull(dto.admin2, dto.admin1).joinToString(" · "),
                            isSpotlight = false,
                        )
                        customCities[city.id] = city
                        city
                    }
            } catch (_: Exception) {
                emptyList()
            }
        }
    }

    private fun inferBiome(lat: Double, lon: Double): BiomeType = when {
        lat > 30.2 -> BiomeType.HIMALAYAN
        lon < 74.5 && lat in 23.0..29.5 -> BiomeType.THAR_DESERT
        lon > 89.5 -> BiomeType.NORTHEAST_HILLS
        lat < 15.5 || lon < 73.2 -> BiomeType.COASTAL
        else -> BiomeType.PLAINS
    }

    private fun mapToCityWeather(
        city: IndianCity,
        resp: OpenMeteoForecastResponse,
        aqi: AirQualityInfo?,
    ): CityWeather {
        val current = resp.current
        val currentIso = current?.time ?: ""

        val hourlyList = buildList {
            val h = resp.hourly
            if (h != null) {
                val count = minOf(h.time.size, h.temperature2m.size, h.weatherCode.size)
                var startIdx = h.time.indexOfFirst { it >= currentIso.take(13) }
                if (startIdx < 0) startIdx = 0
                val endIdx = minOf(count, startIdx + 24)
                for (i in startIdx until endIdx) {
                    val timeStr = h.time[i]
                    val hourOnly = timeStr.substringAfter("T").take(5)
                    add(
                        HourlyForecast(
                            timeIso = timeStr,
                            hourLabel = if (i == startIdx) "Now" else hourOnly,
                            tempCelsius = h.temperature2m[i],
                            condition = WeatherCondition.fromWmoCode(h.weatherCode[i]),
                            precipitationProb = h.precipitationProbability.getOrNull(i) ?: 0,
                            isDay = (h.isDay.getOrNull(i) ?: 1) == 1,
                        ),
                    )
                }
            }
        }

        val inputDateFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val dayNameFmt = SimpleDateFormat("EEE", Locale.US)
        val dailyList = buildList {
            val d = resp.daily
            if (d != null) {
                val count = minOf(d.time.size, d.temperature2mMax.size, d.temperature2mMin.size)
                for (i in 0 until count) {
                    val dateStr = d.time[i]
                    val dayLabel = if (i == 0) {
                        "Today"
                    } else {
                        runCatching {
                            val parsed = inputDateFmt.parse(dateStr)
                            if (parsed != null) dayNameFmt.format(parsed) else dateStr
                        }.getOrDefault(dateStr)
                    }
                    add(
                        DailyForecast(
                            dateIso = dateStr,
                            dayLabel = dayLabel,
                            condition = WeatherCondition.fromWmoCode(d.weatherCode.getOrNull(i) ?: 0),
                            tempMaxCelsius = d.temperature2mMax[i],
                            tempMinCelsius = d.temperature2mMin[i],
                            precipitationProbMax = d.precipitationProbabilityMax.getOrNull(i) ?: 0,
                            uvIndexMax = d.uvIndexMax.getOrNull(i) ?: 0.0,
                            windSpeedMaxKmh = d.windSpeed10mMax.getOrNull(i) ?: 0.0,
                            sunriseIso = d.sunrise.getOrNull(i) ?: "",
                            sunsetIso = d.sunset.getOrNull(i) ?: "",
                        ),
                    )
                }
            }
        }

        return CityWeather(
            city = city,
            tempCelsius = current?.temperature2m ?: 28.0,
            feelsLikeCelsius = current?.apparentTemperature ?: current?.temperature2m ?: 28.0,
            humidityPercent = current?.relativeHumidity2m ?: 50,
            windSpeedKmh = current?.windSpeed10m ?: 8.0,
            windDirectionDeg = current?.windDirection10m ?: 0,
            pressureHpa = current?.surfacePressure ?: 1012.0,
            cloudCoverPercent = current?.cloudCover ?: 10,
            precipitationMm = current?.precipitation ?: 0.0,
            isDay = (current?.isDay ?: 1) == 1,
            condition = WeatherCondition.fromWmoCode(current?.weatherCode ?: 0),
            hourly = hourlyList,
            daily = dailyList,
            airQuality = aqi,
        )
    }
}
