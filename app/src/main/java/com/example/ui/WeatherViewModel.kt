package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.india.IndiaGeoData
import com.example.data.local.PreferencesManager
import com.example.data.model.CitySummaryTemp
import com.example.data.model.IndiaRegion
import com.example.data.model.IndianCity
import com.example.data.model.IndianStateInfo
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherUiState
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WeatherRepository()
    private val preferences = PreferencesManager(application.applicationContext)

    private val _selectedCity = MutableStateFlow(IndiaGeoData.jodhpur)
    val selectedCity: StateFlow<IndianCity> = _selectedCity.asStateFlow()

    private val _weatherUiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherUiState: StateFlow<WeatherUiState> = _weatherUiState.asStateFlow()

    private val _citySummaries = MutableStateFlow<Map<String, CitySummaryTemp>>(emptyMap())
    val citySummaries: StateFlow<Map<String, CitySummaryTemp>> = _citySummaries.asStateFlow()

    private val _selectedRegion = MutableStateFlow(IndiaRegion.ALL)
    val selectedRegion: StateFlow<IndiaRegion> = _selectedRegion.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _remoteSearchResults = MutableStateFlow<List<IndianCity>>(emptyList())
    val remoteSearchResults: StateFlow<List<IndianCity>> = _remoteSearchResults.asStateFlow()

    private val _isSearchingRemote = MutableStateFlow(false)
    val isSearchingRemote: StateFlow<Boolean> = _isSearchingRemote.asStateFlow()

    private val _expandedStateCode = MutableStateFlow<String?>("RJ")
    val expandedStateCode: StateFlow<String?> = _expandedStateCode.asStateFlow()

    val pinnedCityIds: StateFlow<Set<String>> = preferences.pinnedCityIdsFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, IndiaGeoData.defaultPinnedIds)

    val temperatureUnit: StateFlow<TemperatureUnit> = preferences.temperatureUnitFlow
        .stateIn(viewModelScope, SharingStarted.Eagerly, TemperatureUnit.CELSIUS)

    val quickSwitchCities: StateFlow<List<IndianCity>> = pinnedCityIds
        .combine(_selectedCity) { pinned, selected ->
            val priority = listOfNotNull(
                IndiaGeoData.jodhpur,
                IndiaGeoData.barmer,
                IndiaGeoData.mumbai,
            )
            val pinnedList = pinned.mapNotNull { repository.getCityById(it) }
            (listOf(selected) + priority + pinnedList).distinctBy { it.id }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            listOf(IndiaGeoData.jodhpur, IndiaGeoData.barmer, IndiaGeoData.mumbai),
        )

    private var fetchWeatherJob: Job? = null
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            val savedId = preferences.selectedCityIdFlow.first()
            val initialCity = repository.getCityById(savedId) ?: IndiaGeoData.jodhpur
            _selectedCity.value = initialCity
            loadWeatherForCity(initialCity)
            loadInitialSummaries()
        }
    }

    fun selectCity(city: IndianCity) {
        repository.registerCustomCity(city)
        _selectedCity.value = city
        viewModelScope.launch {
            preferences.setSelectedCityId(city.id)
        }
        loadWeatherForCity(city)
    }

    fun refreshSelectedCity() {
        loadWeatherForCity(_selectedCity.value, forceRefresh = true)
        loadInitialSummaries()
    }

    fun togglePinCity(city: IndianCity) {
        repository.registerCustomCity(city)
        viewModelScope.launch {
            preferences.togglePinnedCity(city.id)
        }
    }

    fun setTemperatureUnit(unit: TemperatureUnit) {
        viewModelScope.launch {
            preferences.setTemperatureUnit(unit)
        }
    }

    fun selectRegion(region: IndiaRegion) {
        _selectedRegion.value = region
    }

    fun toggleStateExpanded(stateInfo: IndianStateInfo) {
        val next = if (_expandedStateCode.value == stateInfo.code) null else stateInfo.code
        _expandedStateCode.value = next
        if (next != null) {
            loadSummariesForCities(stateInfo.cities)
        }
    }

    val searchSuggestions: StateFlow<List<IndianCity>> = _searchQuery
        .combine(_remoteSearchResults) { rawQuery, remote ->
            val trimmed = rawQuery.trim()
            if (trimmed.isEmpty()) {
                emptyList()
            } else {
                val localMatches = IndiaGeoData.allCities
                    .filter { city ->
                        city.name.contains(trimmed, ignoreCase = true) ||
                            city.stateName.contains(trimmed, ignoreCase = true) ||
                            city.subtitle.contains(trimmed, ignoreCase = true)
                    }
                    .sortedWith(
                        compareByDescending<IndianCity> { it.name.equals(trimmed, ignoreCase = true) }
                            .thenByDescending { it.name.startsWith(trimmed, ignoreCase = true) }
                            .thenByDescending { it.isSpotlight },
                    )
                (localMatches + remote).distinctBy {
                    "${it.name.lowercase()}_${it.stateName.lowercase()}"
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun clearSearch() {
        searchJob?.cancel()
        _searchQuery.value = ""
        _remoteSearchResults.value = emptyList()
        _isSearchingRemote.value = false
    }

    fun submitCitySearch(query: String) {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return

        val existingMatch = searchSuggestions.value.firstOrNull {
            it.name.equals(trimmed, ignoreCase = true)
        } ?: IndiaGeoData.allCities.firstOrNull {
            it.name.equals(trimmed, ignoreCase = true)
        } ?: searchSuggestions.value.firstOrNull()

        if (existingMatch != null) {
            selectCity(existingMatch)
            clearSearch()
            return
        }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isSearchingRemote.value = true
            val remote = repository.searchIndianCitiesRemote(trimmed)
            _remoteSearchResults.value = remote
            _isSearchingRemote.value = false
            val best = remote.firstOrNull()
            if (best != null) {
                selectCity(best)
                clearSearch()
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.trim().length < 2) {
            _remoteSearchResults.value = emptyList()
            _isSearchingRemote.value = false
            return
        }
        val localTop = IndiaGeoData.allCities.filter {
            it.name.contains(query.trim(), ignoreCase = true)
        }.take(8)
        if (localTop.isNotEmpty()) {
            loadSummariesForCities(localTop)
        }
        searchJob = viewModelScope.launch {
            _isSearchingRemote.value = true
            delay(280L)
            val remote = repository.searchIndianCitiesRemote(query)
            _remoteSearchResults.value = remote
            _isSearchingRemote.value = false
            if (remote.isNotEmpty()) {
                loadSummariesForCities(remote.take(10))
            }
        }
    }

    private fun loadWeatherForCity(city: IndianCity, forceRefresh: Boolean = false) {
        fetchWeatherJob?.cancel()
        fetchWeatherJob = viewModelScope.launch {
            _weatherUiState.value = WeatherUiState.Loading
            val result = repository.getCityWeather(city, forceRefresh = forceRefresh)
            result.fold(
                onSuccess = { weather ->
                    _weatherUiState.value = WeatherUiState.Success(weather)
                    _citySummaries.update { current ->
                        current + (
                            city.id to CitySummaryTemp(
                                cityId = city.id,
                                tempCelsius = weather.tempCelsius,
                                condition = weather.condition,
                                isDay = weather.isDay,
                                windSpeedKmh = weather.windSpeedKmh,
                                humidityPercent = weather.humidityPercent,
                            )
                        )
                    }
                },
                onFailure = { error ->
                    _weatherUiState.value = WeatherUiState.Error(
                        message = error.localizedMessage ?: "Unable to connect to weather service",
                        city = city,
                    )
                },
            )
        }
    }

    private fun loadInitialSummaries() {
        val mapAndSpotlightIds = listOf(
            "jodhpur_rj", "barmer_rj", "mumbai_mh", "jaipur_rj", "new_delhi_dl",
            "srinagar_jk", "leh_la", "ahmedabad_gj", "bengaluru_ka", "chennai_tn",
            "hyderabad_ts", "kolkata_wb", "guwahati_as", "kochi_kl", "bhopal_mp",
            "lucknow_up", "bhubaneswar_od", "panaji_ga", "shimla_hp", "gangtok_sk",
            "port_blair_an", "kavaratti_ld", "patna_br", "raipur_cg", "ranchi_jh",
        )
        val initialBatch = (
            mapAndSpotlightIds.mapNotNull { repository.getCityById(it) } +
                (IndiaGeoData.allStatesAndUTs.firstOrNull { it.code == "RJ" }?.cities ?: emptyList()) +
                (IndiaGeoData.allStatesAndUTs.firstOrNull { it.code == "MH" }?.cities ?: emptyList())
            ).distinctBy { it.id }
        loadSummariesForCities(initialBatch)
    }

    fun loadSummariesForCities(cities: List<IndianCity>) {
        if (cities.isEmpty()) return
        viewModelScope.launch {
            val missing = cities.filter { !_citySummaries.value.containsKey(it.id) }
            val toFetch = if (missing.isEmpty()) cities.take(12) else missing
            val fetched = repository.fetchBatchSummaries(toFetch)
            if (fetched.isNotEmpty()) {
                _citySummaries.update { it + fetched }
            }
        }
    }
}
