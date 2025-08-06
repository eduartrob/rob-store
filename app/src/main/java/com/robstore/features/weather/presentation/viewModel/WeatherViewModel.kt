package com.robstore.features.weather.presentation.viewModel

import WeatherUseCase
import com.robstore.features.weather.domain.model.Weather


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.core.hardware.location.domain.useCase.LocationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherUseCase: WeatherUseCase,
    private val locationUseCase: LocationUseCase
) : ViewModel() {

    private val _weather = MutableStateFlow<Weather?>(null)
    val weather: StateFlow<Weather?> = _weather

    init {
        viewModelScope.launch {
            val result = locationUseCase.getLatLng()
            if (result.isSuccess) {
                val (latitude, longitude) = result.getOrThrow()
                loadWeather(latitude, longitude)
            } else {
                val exception = result.exceptionOrNull()
                Log.e("ViewModel", "Error al obtener la ubicación: ${exception?.message}")
            }
        }
    }

    private fun loadWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            weatherUseCase(lat, lon)
                .onSuccess {
                    _weather.value = it
                }
                .onFailure {
                    Log.e("WeatherViewModel", "Error al obtener clima", it)
                }
        }
    }

}