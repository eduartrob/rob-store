package com.robstore.features.weather.presentation.viewModel

import WeatherUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.core.hardware.location.domain.useCase.LocationUseCase

class WeatherViewModelFactory(
    private val locationUseCase: LocationUseCase,
    private val weatherUseCase: WeatherUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(weatherUseCase, locationUseCase) as T
    }
}
