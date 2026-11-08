package com.example.skysky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
    val navyBlue = Color(0xFF000080)

    // This allows us to control the keyboard (like hiding it after a search)
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        containerColor = navyBlue,
        topBar = {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it },
                placeholder = { Text("Type the city name", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 40.dp, bottom = 16.dp)
                    .background(Color.White, shape = MaterialTheme.shapes.small),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                // 1. Changes the enter key on the keyboard to a "Search" icon/action
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                // 2. Tells the app what to do when that search button is pressed
                keyboardActions = KeyboardActions(
                    onSearch = {
                        viewModel.fetchWeather(city)
                        keyboardController?.hide() // Hides the keyboard after searching
                    }
                ),
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.fetchWeather(city)
                        keyboardController?.hide()
                    }) {
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