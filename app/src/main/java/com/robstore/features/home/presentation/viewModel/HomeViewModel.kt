package com.robstore.features.home.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.core.hardware.internet.domain.useCase.InternetConnectivityUseCase
import com.robstore.core.hardware.location.domain.useCase.LocationUseCase
import com.robstore.core.store.local.dataStore.DataStoreManager
import com.robstore.core.store.local.dataStore.PreferenceKeys
import com.robstore.features.home.domain.useCase.HomeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val locationUseCase: LocationUseCase,
    private val dataStoreManager: DataStoreManager,
    private val homeUseCase: HomeUseCase,
    private val internetConnectivityUseCase: InternetConnectivityUseCase
): ViewModel(){
    private val _country = MutableStateFlow<String?>(null)
    val country: StateFlow<String?> = _country

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl

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
    fun fetchProfileImage() {
        viewModelScope.launch {
            try {
                val result = homeUseCase()
                if(result.isSuccess) {
                    val imgUrl = result.getOrNull()?.imgProfile
                    if (imgUrl != null) {
                        dataStoreManager.saveKey(PreferenceKeys.USER_PROFILE_PICTURE_URI, imgUrl)
                        Log.d("HomeViewModel", "Imagen de perfil guardada en DataStore: $imgUrl")
                    }
                    _profileImageUrl.value = imgUrl
                }

            } catch (e: Exception) {
                // Puedes loguear el error o asignar una imagen por defecto
                _profileImageUrl.value = null
            }
        }
    }
}