package com.robstore.features.home.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.core.hardware.internet.domain.useCase.InternetConnectivityUseCase
import com.robstore.core.hardware.location.domain.useCase.LocationUseCase
import com.robstore.core.store.local.dataStore.DataStoreManager
import com.robstore.features.home.domain.useCase.HomeUseCase

class HomeViewModelFactory(
    private val locationUseCase: LocationUseCase,
    private val dataStoreManager: DataStoreManager,
    private val homeUseCase: HomeUseCase,
    private val internetConnectivityUseCase: InternetConnectivityUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(locationUseCase, dataStoreManager, homeUseCase, internetConnectivityUseCase) as T
    }
}