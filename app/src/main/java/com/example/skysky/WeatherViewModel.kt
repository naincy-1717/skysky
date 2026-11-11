package com.example.skysky

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    // Holds the successful weather data
    private val _weatherData = MutableStateFlow<WeatherResponse?>(null)
    val weatherData: StateFlow<WeatherResponse?> = _weatherData

    // Holds any error messages (like "City not found")
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val apiKey = "f125c0f260f7e23ecbffb7f6b575cc54"

    // Holds the list of suggested cities
    private val _citySuggestions = MutableStateFlow<List<CitySearchResult>>(emptyList())
    val citySuggestions: StateFlow<List<CitySearchResult>> = _citySuggestions
    private val _forecastData = MutableStateFlow<List<ForecastItem>?>(null)
    val forecastData: StateFlow<List<ForecastItem>?> = _forecastData

    fun fetchForecast(cityName: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.getForecast(cityName, apiKey)
                // Filter to get roughly one forecast per day (e.g., midday entries)
                _forecastData.value = response.list.filter { it.dt_txt.contains("12:00:00") }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    // Function to fetch suggestions while typing
    fun getCitySuggestions(searchQuery: String) {
        if (searchQuery.length < 3) {
            _citySuggestions.value = emptyList()
            return
        }
        viewModelScope.launch {
            try {
                val results = RetrofitClient.api.searchCities(searchQuery, 5, apiKey)
                _citySuggestions.value = results
            } catch (e: Exception) {
                _citySuggestions.value = emptyList()
            }
        }
    }

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            try {
                // Makes the background network call
                val response = RetrofitClient.api.getWeather(city, apiKey)
                _weatherData.value = response
                _errorMessage.value = null // Clear any previous errors
            } catch (e: Exception) {
                // If the city is invalid or internet is off, this catches the crash
                _errorMessage.value = "Failed to fetch weather. Check city name or connection."
            }
        }
    }
}