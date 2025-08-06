package com.robstore.features.weather.di

import WeatherRepository
import WeatherRepositoryImpl
import WeatherService
import WeatherUseCase
import com.robstore.core.network.RetrofitHelper
import com.robstore.features.home.di.HomeAppModul.homeRepository
import com.robstore.features.home.domain.useCase.HomeUseCase

object WeatherAppModule {
    private val weatherService: WeatherService by lazy {
        RetrofitHelper.getWeatherService()
    }

    private val weatherRepositoryImpl: WeatherRepositoryImpl by lazy {
        WeatherRepositoryImpl(weatherService)
    }

    private val weatherRepository: WeatherRepository by lazy {
        weatherRepositoryImpl
    }


    object WeatherAppModule {
        fun getWeatherUseCase(): WeatherUseCase{
            return WeatherUseCase(
                weatherRepository
            )
        }
    }


}