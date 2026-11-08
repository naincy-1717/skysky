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
    val citySuggestions by viewModel.citySuggestions.collectAsState()

    var city by remember { mutableStateOf("") }
    var showSearchPopup by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val lightBlue = Color(0xFFADD8E6)
    val navyBlue = Color(0xFF000080)

    Scaffold(
        containerColor = lightBlue,
        floatingActionButton = {
            val currentData = weatherData
            if (currentData != null) {
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
            val currentData = weatherData

            if (currentData != null) {
                Card(
                    modifier = Modifier.padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = currentData.name, style = MaterialTheme.typography.headlineMedium)

                        val iconCode = currentData.weather.firstOrNull()?.icon
                        val iconUrl = "https://openweathermap.org/img/wn/${iconCode}@2x.png"
                        AsyncImage(
                            model = iconUrl,
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(100.dp)
                        )

                        Text(text = "${currentData.main.temp} °C", style = MaterialTheme.typography.displayMedium)
                        Text(text = currentData.weather.firstOrNull()?.description?.uppercase() ?: "")

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Humidity: ${currentData.main.humidity}%")
                        Text(text = "Wind Speed: ${currentData.wind.speed} m/s")
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

            val currentError = errorMessage
            if (currentError != null) {
                Text(
                    text = currentError,
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
                onDismissRequest = {
                    showSearchPopup = false
                    viewModel.getCitySuggestions("")
                },
                title = { Text("Enter City") },
                text = {
                    Column {
                        if (showSearchPopup) {
                            AlertDialog(
                                onDismissRequest = { showSearchPopup = false },
                                title = { Text("Enter City") },
                                text = {
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        OutlinedTextField(
                                            value = city,
                                            onValueChange = { typedText ->
                                                city = typedText
                                                // Only fetch suggestions if the user has typed at least 3 letters
                                                // and has paused/continued typing normally without breaking focus.
                                                if (typedText.length >= 2) {
                                                    viewModel.getCitySuggestions(typedText)
                                                }
                                            },
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

                                        // Dropdown list for similar city suggestions
                                        val citySuggestions by viewModel.citySuggestions.collectAsState()

                                        if (citySuggestions.isNotEmpty()) {
                                            // Keeps the dropdown compact right beneath the text field without forcing the keyboard down
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                elevation = CardDefaults.cardElevation(4.dp),
                                                colors = CardDefaults.cardColors(containerColor = Color.White)
                                            ) {
                                                Column(modifier = Modifier.padding(8.dp)) {
                                                    citySuggestions.forEach { suggestion ->
                                                        TextButton(
                                                            onClick = {
                                                                city = suggestion.name
                                                                viewModel.fetchWeather(suggestion.name)
                                                                showSearchPopup = false
                                                                keyboardController?.hide()
                                                            },
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Text(
                                                                text = "${suggestion.name}, ${suggestion.state ?: ""} (${suggestion.country})",
                                                                color = navyBlue,
                                                                modifier = Modifier.fillMaxWidth()
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(onClick = { showSearchPopup = false }) {
                                        Text("Cancel", color = lightBlue)
                                    }
                                }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        showSearchPopup = false
                        viewModel.getCitySuggestions("")
                    }) {
                        Text("Cancel", color = lightBlue)
                    }
                }
            )
        }
    }
}