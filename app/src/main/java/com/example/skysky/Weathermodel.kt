package com.example.skysky

data class WeatherResponse(
    val name: String,
    val main: MainData,
    val weather: List<WeatherData>,
    val wind: WindData,
    val sys: Sys // Added this missing property
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

data class CitySearchResult(
    val name: String,
    val state: String?,
    val country: String,
    val lat: Double,
    val lon: Double
)

data class Sys(
    val country: String
)