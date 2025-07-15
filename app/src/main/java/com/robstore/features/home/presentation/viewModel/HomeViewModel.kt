package com.robstore.features.home.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.core.hardware.location.domain.useCase.LocationUseCase
import com.robstore.core.store.local.DataStoreManager
import com.robstore.core.store.local.PreferenceKeys
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val locationUseCase: LocationUseCase,
    private val dataStoreManager: DataStoreManager
): ViewModel(){
    private val _country = MutableStateFlow<String?>(null)
    val country: StateFlow<String?> = _country

    fun requestAndSaveCountry() {
        viewModelScope.launch {
            val result = locationUseCase()
            if (result.isSuccess) {
                val countryName = result.getOrNull()
                countryName?.let {
                    dataStoreManager.saveKey(PreferenceKeys.USER_REGION, it)
                    _country.value = it
                }
            } else {
                // Manejo de error (opcional)
                val error = result.exceptionOrNull()
                _country.value = "Ubicación desconocida"
                // Puedes loguear el error si quieres
            }
        }
    }
}