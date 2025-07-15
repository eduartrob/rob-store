package com.robstore.features.home.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.core.hardware.location.domain.useCase.LocationUseCase
import com.robstore.core.store.local.DataStoreManager

class HomeViewModelFactory(
    private val locationUseCase: LocationUseCase,
    private val dataStoreManager: DataStoreManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(locationUseCase, dataStoreManager) as T
    }
}