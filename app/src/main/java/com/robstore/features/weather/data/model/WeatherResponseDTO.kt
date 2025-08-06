
package com.robstore.features.weather.data.model


data class WeatherResponseDTO(
    val latitude: Double,
    val longitude: Double,
    val current_weather: CurrentWeatherDTO
)

data class CurrentWeatherDTO(
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Double,
    val weathercode: Int,
    val time: String
)
