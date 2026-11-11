package com.example.skysky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
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
    val forecastData by viewModel.forecastData.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var city by remember { mutableStateOf("") }
    var showSearchPopup by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    val backgroundColor = MaterialTheme.colorScheme.background
    val textColor = MaterialTheme.colorScheme.onBackground
    val cardColor = MaterialTheme.colorScheme.surface
    val onCardTextColor = MaterialTheme.colorScheme.onSurface

    val isDark = isSystemInDarkTheme()
    val whiteCloud = if (isDark) Color(0xFFBBBBBB) else Color(0xFFFFFFFF)
    val lightGrayCloud = if (isDark) Color(0xFF888888) else Color(0xFFE8F1F5)

    // Detect weather type for animations (Rain, Snow, Sun/Clear)
    val weatherDesc = weatherData?.weather?.firstOrNull()?.description?.lowercase() ?: ""
    val isRaining = weatherDesc.contains("rain") || weatherDesc.contains("drizzle")
    val isSnowing = weatherDesc.contains("snow")
    val isSunny = weatherDesc.contains("clear") || weatherDesc.contains("sun")

    // Infinite transition for continuous particle animations
    val infiniteTransition = rememberInfiniteTransition(label = "weatherAnimation")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        // Background Grid & Weather Animations Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val gridStep = 60.dp.toPx()
            val gridColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.4f)

            // Grid lines
            var x = 0f
            while (x < canvasWidth) {
                drawLine(color = gridColor, start = Offset(x, 0f), end = Offset(x, canvasHeight), strokeWidth = 2f)
                x += gridStep
            }
            var y = 0f
            while (y < canvasHeight) {
                drawLine(color = gridColor, start = Offset(0f, y), end = Offset(canvasWidth, y), strokeWidth = 2f)
                y += gridStep
            }

            // --- ANIMATION EFFECTS ---
            if (isRaining) {
                // Falling raindrops effect
                val dropCount = 40
                for (i in 0 until dropCount) {
                    val startX = (i * 37f) % canvasWidth
                    val startY = ((i * 73f) + (animProgress * canvasHeight)) % canvasHeight
                    drawLine(
                        color = Color.Cyan.copy(alpha = 0.6f),
                        start = Offset(startX, startY),
                        end = Offset(startX - 5f, startY + 20f),
                        strokeWidth = 3f
                    )
                }
            } else if (isSnowing) {
                // Falling snowflakes effect
                val flakeCount = 30
                for (i in 0 until flakeCount) {
                    val flakeX = ((i * 53f) + kotlin.math.sin(animProgress * 6.28f + i) * 20f) % canvasWidth
                    val flakeY = ((i * 91f) + (animProgress * canvasHeight)) % canvasHeight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.8f),
                        radius = (i % 3 + 3).toFloat(),
                        center = Offset(flakeX, flakeY)
                    )
                }
            } else if (isSunny) {
                // Pulsing sun rays / glowing background circles
                val centerX = canvasWidth / 2f
                val centerY = 200f
                val radius = 80f + (animProgress * 20f)
                drawCircle(
                    color = Color.Yellow.copy(alpha = 0.15f),
                    radius = radius,
                    center = Offset(centerX, centerY)
                )
            }
        }

        // Decorative Clouds
        Icon(
            imageVector = Icons.Default.Cloud,
            contentDescription = null,
            tint = whiteCloud.copy(alpha = 0.7f),
            modifier = Modifier.align(Alignment.TopStart).offset(x = 30.dp, y = 80.dp).size(100.dp)
        )
        Icon(
            imageVector = Icons.Default.Cloud,
            contentDescription = null,
            tint = lightGrayCloud.copy(alpha = 0.8f),
            modifier = Modifier.align(Alignment.TopEnd).offset(x = (-20).dp, y = 140.dp).size(120.dp)
        )

        // Main Foreground Content
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hello user !",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = textColor,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "want to see the city details :)",
                style = MaterialTheme.typography.titleMedium,
                color = textColor.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            FloatingActionButton(
                onClick = { showSearchPopup = true },
                containerColor = cardColor,
                contentColor = textColor,
                elevation = FloatingActionButtonDefaults.elevation(8.dp),
                shape = CircleShape,
                modifier = Modifier.size(80.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Search City", modifier = Modifier.size(40.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Tap to enter the city",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = textColor.copy(alpha = 0.9f)
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.background(cardColor.copy(alpha = 0.9f), shape = MaterialTheme.shapes.small).padding(16.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }

            weatherData?.let { weather ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardColor.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${weather.name}, ${weather.sys.country}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = onCardTextColor
                        )

                        val iconUrl = "https://openweathermap.org/img/wn/${weather.weather.firstOrNull()?.icon}@2x.png"
                        AsyncImage(model = iconUrl, contentDescription = "Weather Icon", modifier = Modifier.size(90.dp))

                        Text(
                            text = "${weather.main.temp} °C",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = onCardTextColor
                        )
                        Text(
                            text = weather.weather.firstOrNull()?.description?.uppercase() ?: "",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isDark) Color.LightGray else Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = if (isDark) Color.DarkGray else Color.LightGray, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Humidity", color = if (isDark) Color.LightGray else Color.Gray)
                                Text("${weather.main.humidity}%", fontWeight = FontWeight.Bold, color = onCardTextColor)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Wind", color = if (isDark) Color.LightGray else Color.Gray)
                                Text("${weather.wind.speed} m/s", fontWeight = FontWeight.Bold, color = onCardTextColor)
                            }
                        }

                        // 5-Day Forecast Section
                        forecastData?.let { list ->
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(color = if (isDark) Color.DarkGray else Color.LightGray, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "5-Day Forecast",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = onCardTextColor
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                list.forEach { item ->
                                    val dateStr = item.dt_txt.split(" ")[0]
                                    val dayName = dateStr.takeLast(5)

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = dayName, style = MaterialTheme.typography.bodySmall, color = onCardTextColor)
                                        val forecastIconUrl = "https://openweathermap.org/img/wn/${item.weather.firstOrNull()?.icon}.png"
                                        AsyncImage(model = forecastIconUrl, contentDescription = null, modifier = Modifier.size(36.dp))
                                        Text(text = "${item.main.temp.toInt()}°C", fontWeight = FontWeight.Bold, color = onCardTextColor)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search Popup Dialog
        if (showSearchPopup) {
            AlertDialog(
                onDismissRequest = {
                    showSearchPopup = false
                    viewModel.getCitySuggestions("")
                },
                title = { Text("Enter City", color = onCardTextColor) },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = city,
                            onValueChange = { typedText ->
                                city = typedText
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
                                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = textColor)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                cursorColor = MaterialTheme.colorScheme.primary,
                                focusedTextColor = onCardTextColor,
                                unfocusedTextColor = onCardTextColor
                            )
                        )

                        val citySuggestions by viewModel.citySuggestions.collectAsState()

                        if (citySuggestions.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
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
                                                color = onCardTextColor,
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
                    TextButton(onClick = {
                        showSearchPopup = false
                        viewModel.getCitySuggestions("")
                    }) {
                        Text("Cancel", color = MaterialTheme.colorScheme.primary)
                    }
                },
                containerColor = cardColor
            )
        }
    }
}