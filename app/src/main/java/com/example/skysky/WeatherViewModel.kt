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