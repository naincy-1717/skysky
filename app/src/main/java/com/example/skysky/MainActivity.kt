package com.example.skysky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                WeatherScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val weatherData by viewModel.weatherData.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var city by remember { mutableStateOf("") }
    val navyBlue = Color(0xFF000080)

    Scaffold(
        containerColor = navyBlue,
        topBar = {
            // Search bar is moved lower using top = 40.dp
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                placeholder = { Text("Type city name", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 80.dp, bottom = 16.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.small),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                // Small world icon added at the end of the toolbar
                trailingIcon = {
                    IconButton(onClick = { viewModel.fetchWeather(city) }) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Search World",
                            tint = navyBlue
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // The plus sign has been completely removed from this area

            // Display weather data if available
            if (weatherData != null) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = weatherData!!.name, style = MaterialTheme.typography.headlineMedium)

                        val iconUrl = "https://openweathermap.org/img/wn/${weatherData!!.weather.firstOrNull()?.icon}@2x.png"
                        AsyncImage(
                            model = iconUrl,
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(100.dp)
                        )

                        Text(text = "${weatherData!!.main.temp} °C", style = MaterialTheme.typography.displayMedium)
                        Text(text = weatherData!!.weather.firstOrNull()?.description?.uppercase() ?: "")

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Humidity: ${weatherData!!.main.humidity}%")
                        Text(text = "Wind Speed: ${weatherData!!.wind.speed} m/s")
                    }
                }
            }

            // Error message display at the bottom
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .background(Color.White, shape = MaterialTheme.shapes.small)
                        .padding(8.dp)
                )
            }
        }
    }
}