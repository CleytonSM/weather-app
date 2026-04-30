package com.project.weatherapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.clickable
import com.project.weatherapp.ui.components.GlassTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.weatherapp.network.GeocodingResult
import com.project.weatherapp.network.RetrofitInstance
import com.project.weatherapp.network.WeatherResponse
import com.project.weatherapp.ui.components.GlassContainer
import com.project.weatherapp.ui.theme.TextPrimary
import com.project.weatherapp.ui.theme.BluePrimary
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HomeScreen() {
    var weatherData by remember { mutableStateOf<WeatherResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    var selectedLocationName by remember { mutableStateOf("San Francisco") }
    var selectedLat by remember { mutableStateOf(37.7749) }
    var selectedLon by remember { mutableStateOf(-122.4194) }

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<GeocodingResult>?>(null) }

    LaunchedEffect(selectedLat, selectedLon) {
        coroutineScope.launch {
            isLoading = true
            try {
                weatherData = RetrofitInstance.api.getWeatherForecast(selectedLat, selectedLon)
                isLoading = false
            } catch (e: Exception) {
                errorMessage = e.message
                isLoading = false
            }
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length > 2) {
            try {
                val response = RetrofitInstance.geocodingApi.searchLocation(searchQuery)
                searchResults = response.results
            } catch (e: Exception) {
                // Ignore search errors
            }
        } else {
            searchResults = null
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BluePrimary)
        }
    } else if (errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Error: $errorMessage", color = TextPrimary)
        }
    } else {
        weatherData?.let { data ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
            ) {
                item {
                    if (isSearching) {
                        SearchSection(
                            searchQuery = searchQuery,
                            onSearchQueryChange = { searchQuery = it },
                            onClose = {
                                isSearching = false
                                searchQuery = ""
                                searchResults = null
                            },
                            searchResults = searchResults,
                            onResultSelected = { result ->
                                selectedLocationName = result.name
                                selectedLat = result.latitude
                                selectedLon = result.longitude
                                isSearching = false
                                searchQuery = ""
                                searchResults = null
                            }
                        )
                    } else {
                        HeaderSection(
                            locationName = selectedLocationName,
                            temperature = data.current.temperature, 
                            weatherCode = data.current.weatherCode,
                            onSearchClick = { isSearching = true }
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                item {
                    Text(
                        "Hourly Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    HourlyForecastSection(data)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        GlassContainer(modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Air, contentDescription = "Wind", tint = BluePrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Wind", style = MaterialTheme.typography.bodyMedium, color = TextPrimary.copy(alpha = 0.7f))
                                Text("${data.current.windSpeed} km/h", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            }
                        }
                        GlassContainer(modifier = Modifier.weight(1f)) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.WaterDrop, contentDescription = "Humidity", tint = BluePrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Humidity", style = MaterialTheme.typography.bodyMedium, color = TextPrimary.copy(alpha = 0.7f))
                                Text("${data.current.humidity}%", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        "7-Day Forecast",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    DailyForecastSection(data)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    AirQualityWidget()
                }
            }
        }
    }
}

@Composable
fun HeaderSection(locationName: String, temperature: Double, weatherCode: Int, onSearchClick: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Cloud, contentDescription = null, tint = BluePrimary)
            Text("SkyCast", style = MaterialTheme.typography.titleMedium, color = BluePrimary)
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = BluePrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(locationName, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${temperature.toInt()}°",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = getWeatherIcon(weatherCode),
                contentDescription = "Weather condition",
                tint = BluePrimary,
                modifier = Modifier.size(64.dp)
            )
        }
        
        Text(
            text = getWeatherDescription(weatherCode),
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun HourlyForecastSection(data: WeatherResponse) {
    // Get next 24 hours
    val currentHourIndex = data.hourly.time.indexOfFirst { 
        try {
            LocalDateTime.parse(it).isAfter(LocalDateTime.now())
        } catch (e: Exception) { false }
    }.takeIf { it != -1 } ?: 0

    val maxIndex = minOf(currentHourIndex + 24, data.hourly.time.size)
    val displayData = data.hourly.time.subList(currentHourIndex, maxIndex).mapIndexed { i, timeStr ->
        val idx = currentHourIndex + i
        Triple(timeStr, data.hourly.temperatures[idx], data.hourly.weatherCodes[idx])
    }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        itemsIndexed(displayData) { _, item ->
            val time = try {
                LocalDateTime.parse(item.first).format(DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: Exception) { "00:00" }

            GlassContainer(modifier = Modifier.width(80.dp)) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(time, style = MaterialTheme.typography.bodySmall, color = TextPrimary.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(getWeatherIcon(item.third), contentDescription = null, tint = BluePrimary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${item.second.toInt()}°", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                }
            }
        }
    }
}

@Composable
fun DailyForecastSection(data: WeatherResponse) {
    GlassContainer(modifier = Modifier.fillMaxWidth()) {
        Column {
            data.daily.time.take(7).forEachIndexed { i, timeStr ->
                val day = try {
                    java.time.LocalDate.parse(timeStr)
                        .dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                } catch (e: Exception) { "Day" }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(day, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, modifier = Modifier.weight(1f))
                    Icon(getWeatherIcon(data.daily.weatherCodes[i]), contentDescription = null, tint = BluePrimary, modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
                        Text("${data.daily.maxTemperatures[i].toInt()}°", style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("${data.daily.minTemperatures[i].toInt()}°", style = MaterialTheme.typography.bodyLarge, color = TextPrimary.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun AirQualityWidget() {
    Text(
        "Air Quality",
        style = MaterialTheme.typography.titleMedium,
        color = TextPrimary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
    GlassContainer(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color(0xFF4CAF50), shape = androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Good", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Text("Air quality is considered satisfactory.", style = MaterialTheme.typography.bodySmall, color = TextPrimary.copy(alpha = 0.7f))
            }
        }
    }
}

// Simple mapping from Open-Meteo WMO weather codes to Material Icons
fun getWeatherIcon(code: Int): androidx.compose.ui.graphics.vector.ImageVector {
    return when (code) {
        0 -> Icons.Default.WbSunny
        1, 2, 3 -> Icons.Default.WbCloudy
        45, 48 -> Icons.Default.Cloud
        51, 53, 55, 56, 57 -> Icons.Default.WaterDrop // Drizzle
        61, 63, 65, 66, 67 -> Icons.Default.WaterDrop // Rain
        71, 73, 75, 77 -> Icons.Default.Cloud // Snow
        80, 81, 82 -> Icons.Default.WaterDrop // Rain showers
        85, 86 -> Icons.Default.Cloud // Snow showers
        95, 96, 99 -> Icons.Default.Thunderstorm // Thunderstorm
        else -> Icons.Default.WbSunny
    }
}

fun getWeatherDescription(code: Int): String {
    return when (code) {
        0 -> "Clear sky"
        1, 2, 3 -> "Partly cloudy"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        61, 63, 65 -> "Rain"
        71, 73, 75 -> "Snow"
        80, 81, 82 -> "Rain showers"
        95 -> "Thunderstorm"
        else -> "Unknown"
    }
}

@Composable
fun SearchSection(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    searchResults: List<GeocodingResult>?,
    onResultSelected: (GeocodingResult) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                GlassTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    label = "Search city",
                    icon = Icons.Default.Search,
                    trailingIcon = {
                        IconButton(onClick = onClose) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextPrimary)
                        }
                    }
                )
            }
        }
        
        if (!searchResults.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            GlassContainer(modifier = Modifier.fillMaxWidth()) {
                Column {
                    searchResults.forEachIndexed { index, result ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onResultSelected(result) }
                                .padding(vertical = 12.dp, horizontal = 8.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = BluePrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${result.name}${if (result.country != null) ", ${result.country}" else ""}", 
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                        }
                        if (index < searchResults.size - 1) {
                            Divider(color = TextPrimary.copy(alpha = 0.2f))
                        }
                    }
                }
            }
        }
    }
}
