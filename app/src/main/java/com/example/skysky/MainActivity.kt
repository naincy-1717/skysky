package com.example.skysky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    // Connects the UI directly to your background ViewModel
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

    // Remembers the city typed in the search bar
    var city by remember { mutableStateOf("") }

    // Navy Blue background color
    val navyBlue = Color(0xFF000080)

    Scaffold(
        containerColor = navyBlue,
        topBar = {
            // Search bar at the top
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                placeholder = { Text("Enter City Name", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.small),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // Display weather data if available, otherwise show the Plus icon
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
            } else {
                // Circular Plus Button in the exact middle of the screen
                FloatingActionButton(
                    onClick = { viewModel.fetchWeather(city) },
                    shape = CircleShape,
                    containerColor = Color.White,
                    contentColor = navyBlue,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Enter City",
                        modifier = Modifier.size(36.dp)
                    )
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