package com.robstore.features.home.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.core.sync.internet.domain.useCase.InternetConnectivityUseCase
import com.robstore.core.hardware.location.domain.useCase.LocationUseCase
import com.robstore.core.store.local.dataStore.DataStoreManager
import com.robstore.core.store.local.dataStore.PreferenceKeys
import com.robstore.features.home.domain.useCase.HomeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel(
    private val locationUseCase: LocationUseCase,
    private val dataStoreManager: DataStoreManager,
    private val homeUseCase: HomeUseCase,
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
                val error = result.exceptionOrNull()
                _country.value = "Ubicación desconocida"
            }
        }
    }
    fun fetchProfileImage() {
        viewModelScope.launch {
            try {
                val result = homeUseCase()
                if(result.isSuccess) {
                    val newUrl = result.getOrNull()?.imgProfile

                    // Obtener el valor actual del DataStore de forma "one-shot"
                    val localUrl = dataStoreManager.getKey(PreferenceKeys.USER_PROFILE_PICTURE_URI).first()


                    if (newUrl != null && newUrl != localUrl) {
                        dataStoreManager.saveKey(
                            PreferenceKeys.USER_PROFILE_PICTURE_URI,
                            newUrl
                        )
                        Log.d(
                            "HomeViewModel",
                            "Imagen de perfil guardada en DataStore: $newUrl"
                        )
                    } else {
                        Log.d("HomeViewModel", "La URL de la foto de perfil no ha cambiado. No se necesita actualización.")
                    }
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error al actualizar perfil:")
            }
        }
    }
}