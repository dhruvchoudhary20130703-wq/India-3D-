package com.example

import com.example.data.india.IndiaGeoData
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCondition
import com.example.data.model.formatTemp
import com.example.data.remote.OpenMeteoForecastResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun allIndiaStatesAndUTsAreCovered() {
        val states = IndiaGeoData.allStatesAndUTs
        assertEquals(36, states.size)
        assertEquals(28, states.count { !it.isUnionTerritory })
        assertEquals(8, states.count { it.isUnionTerritory })
    }

    @Test
    fun spotlightCitiesAndUniqueIdsExist() {
        assertNotNull(IndiaGeoData.findCityById("jodhpur_rj"))
        assertNotNull(IndiaGeoData.findCityById("barmer_rj"))
        assertNotNull(IndiaGeoData.findCityById("mumbai_mh"))

        val allIds = IndiaGeoData.allCities.map { it.id }
        assertEquals(allIds.size, allIds.toSet().size)
        assertTrue(IndiaGeoData.allCities.size >= 110)
    }

    @Test
    fun wmoMappingAndUnitConversionWork() {
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromWmoCode(0))
        assertEquals(WeatherCondition.THUNDERSTORM, WeatherCondition.fromWmoCode(95))
        assertEquals(WeatherCondition.RAIN, WeatherCondition.fromWmoCode(63))

        assertEquals("35°", 35.0.formatTemp(TemperatureUnit.CELSIUS))
        assertEquals("95°", 35.0.formatTemp(TemperatureUnit.FAHRENHEIT))
    }

    @Test
    fun moshiParsesOpenMeteoForecastWithoutKspCodegen() {
        val sampleJson = """
            {
              "latitude": 26.25,
              "longitude": 73.0,
              "current": {
                "time": "2026-11-14T10:00",
                "temperature_2m": 33.4,
                "relative_humidity_2m": 24,
                "apparent_temperature": 32.1,
                "is_day": 1,
                "precipitation": 0.0,
                "weather_code": 0,
                "cloud_cover": 5,
                "surface_pressure": 1012.5,
                "wind_speed_10m": 11.2,
                "wind_direction_10m": 250
              }
            }
        """.trimIndent()
        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
        val adapter = moshi.adapter(OpenMeteoForecastResponse::class.java)
        val parsed = adapter.fromJson(sampleJson)
        assertNotNull(parsed)
        assertEquals(33.4, parsed!!.current!!.temperature2m, 0.01)
        assertEquals(24, parsed.current!!.relativeHumidity2m)
    }
}
