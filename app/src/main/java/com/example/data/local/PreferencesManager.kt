package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.india.IndiaGeoData
import com.example.data.model.TemperatureUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mausam_3d_prefs")

class PreferencesManager(private val context: Context) {

    private object Keys {
        val SELECTED_CITY_ID = stringPreferencesKey("selected_city_id")
        val PINNED_CITY_IDS = stringSetPreferencesKey("pinned_city_ids")
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
    }

    val selectedCityIdFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.SELECTED_CITY_ID] ?: IndiaGeoData.jodhpur.id
    }

    val pinnedCityIdsFlow: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        prefs[Keys.PINNED_CITY_IDS] ?: IndiaGeoData.defaultPinnedIds
    }

    val temperatureUnitFlow: Flow<TemperatureUnit> = context.dataStore.data.map { prefs ->
        when (prefs[Keys.TEMP_UNIT]) {
            TemperatureUnit.FAHRENHEIT.name -> TemperatureUnit.FAHRENHEIT
            else -> TemperatureUnit.CELSIUS
        }
    }

    suspend fun setSelectedCityId(cityId: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SELECTED_CITY_ID] = cityId
        }
    }

    suspend fun togglePinnedCity(cityId: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.PINNED_CITY_IDS] ?: IndiaGeoData.defaultPinnedIds
            val updated = current.toMutableSet()
            if (updated.contains(cityId)) {
                if (updated.size > 1) updated.remove(cityId)
            } else {
                updated.add(cityId)
            }
            prefs[Keys.PINNED_CITY_IDS] = updated
        }
    }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.dataStore.edit { prefs ->
            prefs[Keys.TEMP_UNIT] = unit.name
        }
    }
}
