package com.example.skysky

// This represents the main response from OpenWeatherMap.
// This translates the raw, external data .
// The app's code can easily read, manage, and display on the screen.

data class WeatherResponse(
    val name: String, // City name
    val main: MainData, // Temperature and humidity
    val weather: List<WeatherData>, // Weather conditions and icons
    val wind: WindData // Wind speed
)

data class MainData(
    val temp: Double,
    val humidity: Int
)

data class WeatherData(
    val main: String,
    val description: String,
    val icon: String
)

data class WindData(
    val speed: Double
)
