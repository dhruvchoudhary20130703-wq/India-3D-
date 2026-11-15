package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocationCity
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LocationCity
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.india.IndiaGeoData
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCondition
import com.example.data.model.WeatherUiState
import com.example.ui.WeatherViewModel
import com.example.ui.components.atmosphereBackgroundColors
import com.example.ui.screens.CitiesCompareScreen
import com.example.ui.screens.India3DExplorerScreen
import com.example.ui.screens.Sky3DScreen
import com.example.ui.theme.Mausam3DTheme
import com.example.ui.theme.MonsoonCyan
import com.example.ui.theme.SolarAmber

const val ROUTE_SKY_3D = "sky_3d"
const val ROUTE_INDIA_3D = "india_3d"
const val ROUTE_CITIES = "cities"

private data class NavDestinationItem(
    val route: String,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String,
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            Mausam3DTheme(darkTheme = true, dynamicColor = false) {
                Mausam3DApp()
            }
        }
    }
}

@Composable
fun Mausam3DApp(
    viewModel: WeatherViewModel = viewModel(),
) {
    val selectedCity by viewModel.selectedCity.collectAsStateWithLifecycle()
    val weatherUiState by viewModel.weatherUiState.collectAsStateWithLifecycle()
    val quickSwitchCities by viewModel.quickSwitchCities.collectAsStateWithLifecycle()
    val citySummaries by viewModel.citySummaries.collectAsStateWithLifecycle()
    val pinnedCityIds by viewModel.pinnedCityIds.collectAsStateWithLifecycle()
    val temperatureUnit by viewModel.temperatureUnit.collectAsStateWithLifecycle()
    val selectedRegion by viewModel.selectedRegion.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchSuggestions by viewModel.searchSuggestions.collectAsStateWithLifecycle()
    val remoteSearchResults by viewModel.remoteSearchResults.collectAsStateWithLifecycle()
    val isSearchingRemote by viewModel.isSearchingRemote.collectAsStateWithLifecycle()
    val expandedStateCode by viewModel.expandedStateCode.collectAsStateWithLifecycle()

    val pinnedCities = pinnedCityIds.mapNotNull { id ->
        IndiaGeoData.findCityById(id) ?: if (selectedCity.id == id) selectedCity else null
    }

    val activeCondition = (weatherUiState as? WeatherUiState.Success)?.weather?.condition ?: WeatherCondition.CLEAR
    val activeIsDay = (weatherUiState as? WeatherUiState.Success)?.weather?.isDay ?: true
    val targetGradient = atmosphereBackgroundColors(
        condition = activeCondition,
        biome = selectedCity.biome,
        isDay = activeIsDay,
    )

    val topColor by animateColorAsState(
        targetValue = targetGradient[0],
        animationSpec = tween(durationMillis = 700),
        label = "bg_top",
    )
    val midColor by animateColorAsState(
        targetValue = targetGradient[1],
        animationSpec = tween(durationMillis = 700),
        label = "bg_mid",
    )
    val botColor by animateColorAsState(
        targetValue = targetGradient[2],
        animationSpec = tween(durationMillis = 700),
        label = "bg_bot",
    )

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_SKY_3D

    val navItems = listOf(
        NavDestinationItem(
            route = ROUTE_SKY_3D,
            labelRes = R.string.nav_sky_3d,
            selectedIcon = Icons.Rounded.WbSunny,
            unselectedIcon = Icons.Outlined.WbSunny,
            testTag = "nav_sky_3d",
        ),
        NavDestinationItem(
            route = ROUTE_INDIA_3D,
            labelRes = R.string.nav_india_3d,
            selectedIcon = Icons.Rounded.Explore,
            unselectedIcon = Icons.Outlined.Explore,
            testTag = "nav_india_3d",
        ),
        NavDestinationItem(
            route = ROUTE_CITIES,
            labelRes = R.string.nav_saved,
            selectedIcon = Icons.Rounded.LocationCity,
            unselectedIcon = Icons.Outlined.LocationCity,
            testTag = "nav_cities",
        ),
    )

    fun navigateToTab(route: String) {
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(ROUTE_SKY_3D) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(colors = listOf(topColor, midColor, botColor)),
            ),
    ) {
        val useRail = maxWidth >= 720.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        containerColor = Color(0xFF071024).copy(alpha = 0.86f),
                        tonalElevation = 0.dp,
                    ) {
                        navItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { navigateToTab(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = stringResource(item.labelRes),
                                    )
                                },
                                label = { Text(text = stringResource(item.labelRes)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SolarAmber,
                                    selectedTextColor = Color.White,
                                    indicatorColor = SolarAmber.copy(alpha = 0.18f),
                                    unselectedIconColor = Color.White.copy(alpha = 0.65f),
                                    unselectedTextColor = Color.White.copy(alpha = 0.65f),
                                ),
                                modifier = Modifier.testTag(item.testTag),
                            )
                        }
                    }
                }
            },
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                if (useRail) {
                    NavigationRail(
                        containerColor = Color(0xFF071024).copy(alpha = 0.65f),
                        modifier = Modifier.fillMaxHeight(),
                    ) {
                        navItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationRailItem(
                                selected = selected,
                                onClick = { navigateToTab(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = stringResource(item.labelRes),
                                    )
                                },
                                label = { Text(text = stringResource(item.labelRes)) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = SolarAmber,
                                    selectedTextColor = Color.White,
                                    indicatorColor = MonsoonCyan.copy(alpha = 0.22f),
                                    unselectedIconColor = Color.White.copy(alpha = 0.68f),
                                    unselectedTextColor = Color.White.copy(alpha = 0.68f),
                                ),
                                modifier = Modifier.testTag(item.testTag),
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_SKY_3D,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        composable(ROUTE_SKY_3D) {
                            Sky3DScreen(
                                selectedCity = selectedCity,
                                weatherUiState = weatherUiState,
                                quickSwitchCities = quickSwitchCities,
                                citySummaries = citySummaries,
                                pinnedCityIds = pinnedCityIds,
                                unit = temperatureUnit,
                                searchQuery = searchQuery,
                                searchSuggestions = searchSuggestions,
                                isSearchingRemote = isSearchingRemote,
                                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                onSubmitSearch = { viewModel.submitCitySearch(it) },
                                onSelectSearchResult = { city ->
                                    viewModel.selectCity(city)
                                    viewModel.clearSearch()
                                },
                                onSelectCity = { viewModel.selectCity(it) },
                                onTogglePin = { viewModel.togglePinCity(it) },
                                onRefresh = { viewModel.refreshSelectedCity() },
                                onToggleUnit = {
                                    val next = if (temperatureUnit == TemperatureUnit.CELSIUS) {
                                        TemperatureUnit.FAHRENHEIT
                                    } else {
                                        TemperatureUnit.CELSIUS
                                    }
                                    viewModel.setTemperatureUnit(next)
                                },
                                onOpenExplorer = { navigateToTab(ROUTE_INDIA_3D) },
                            )
                        }

                        composable(ROUTE_INDIA_3D) {
                            BackHandler { navigateToTab(ROUTE_SKY_3D) }
                            India3DExplorerScreen(
                                selectedCity = selectedCity,
                                selectedRegion = selectedRegion,
                                searchQuery = searchQuery,
                                remoteSearchResults = remoteSearchResults,
                                isSearchingRemote = isSearchingRemote,
                                expandedStateCode = expandedStateCode,
                                citySummaries = citySummaries,
                                pinnedCityIds = pinnedCityIds,
                                unit = temperatureUnit,
                                onSelectRegion = { viewModel.selectRegion(it) },
                                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                                onSubmitSearch = { query ->
                                    viewModel.submitCitySearch(query)
                                    navigateToTab(ROUTE_SKY_3D)
                                },
                                onToggleStateExpanded = { viewModel.toggleStateExpanded(it) },
                                onSelectCityAndOpen3D = { city ->
                                    viewModel.selectCity(city)
                                    viewModel.clearSearch()
                                    navigateToTab(ROUTE_SKY_3D)
                                },
                                onSelectCityOnMap = { city ->
                                    viewModel.selectCity(city)
                                },
                                onTogglePin = { viewModel.togglePinCity(it) },
                            )
                        }

                        composable(ROUTE_CITIES) {
                            BackHandler { navigateToTab(ROUTE_SKY_3D) }
                            CitiesCompareScreen(
                                selectedCity = selectedCity,
                                pinnedCities = pinnedCities,
                                citySummaries = citySummaries,
                                unit = temperatureUnit,
                                onSelectCityAndOpen3D = { city ->
                                    viewModel.selectCity(city)
                                    navigateToTab(ROUTE_SKY_3D)
                                },
                                onTogglePin = { viewModel.togglePinCity(it) },
                                onSetUnit = { viewModel.setTemperatureUnit(it) },
                                onOpenExplorer = { navigateToTab(ROUTE_INDIA_3D) },
                            )
                        }
                    }
                }
            }
        }
    }
}
