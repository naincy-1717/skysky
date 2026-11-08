package com.example.skysky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
    var showSearchPopup by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val lightBlue = Color(0xFFADD8E6)
    val navyBlue = Color(0xFF000080)

    Scaffold(
        containerColor = lightBlue,
        floatingActionButton = {
            if (weatherData != null) {
                FloatingActionButton(
                    onClick = { showSearchPopup = true },
                    containerColor = Color.White,
                    contentColor = lightBlue
                ) {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search Again")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
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
                // Column added here to stack the button and the text instruction
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FloatingActionButton(
                        onClick = { showSearchPopup = true },
                        shape = CircleShape,
                        containerColor = Color.White,
                        contentColor = lightBlue,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add City",
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // The text line guiding the user
                    Text(
                        text = "Tap to enter the city",
                        color = navyBlue,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

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

        if (showSearchPopup) {
            AlertDialog(
                onDismissRequest = { showSearchPopup = false },
                title = { Text("Enter City") },
                text = {
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        placeholder = { Text("Type the city name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                viewModel.fetchWeather(city)
                                showSearchPopup = false
                                keyboardController?.hide()
                            }
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                viewModel.fetchWeather(city)
                                showSearchPopup = false
                                keyboardController?.hide()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Public,
                                    contentDescription = "Search World",
                                    tint = lightBlue
                                )
                            }
                        }
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showSearchPopup = false }) {
                        Text("Cancel", color = lightBlue)
                    }
                }
            )
        }
    }
}