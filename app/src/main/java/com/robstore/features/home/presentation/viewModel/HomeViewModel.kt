package com.robstore.features.home.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.core.di.HardwareModule.internetConnectivityUseCase
import com.robstore.core.sync.internet.domain.useCase.InternetConnectivityUseCase
import com.robstore.core.hardware.location.domain.useCase.LocationUseCase
import com.robstore.core.store.local.dataStore.DataStoreManager
import com.robstore.core.store.local.dataStore.PreferenceKeys
import com.robstore.features.home.domain.model.App
import com.robstore.features.home.domain.model.AppUIDetails
import com.robstore.features.home.domain.useCase.HomeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _appList = MutableStateFlow<List<App>>(emptyList())
    val appList: StateFlow<List<App>> = _appList.asStateFlow()

    private val _appsLoading = MutableStateFlow(false)
    val appsLoading: StateFlow<Boolean> = _appsLoading.asStateFlow()

    private val _appsError = MutableStateFlow<String?>(null)
    val appsError: StateFlow<String?> = _appsError.asStateFlow()

    private val _selectedAppFiles = MutableStateFlow<App?>(null)
    val selectedAppFiles: StateFlow<App?> = _selectedAppFiles.asStateFlow()

    private val _appFilesLoading = MutableStateFlow(false)
    val appFilesLoading: StateFlow<Boolean> = _appFilesLoading.asStateFlow()

    private val _appFilesError = MutableStateFlow<String?>(null)
    val appFilesError: StateFlow<String?> = _appFilesError.asStateFlow()


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


    fun fetchApps() {
        viewModelScope.launch {
            _appsLoading.value = true
            _appsError.value = null


            try {
                // 1. Obtener la lista básica de aplicaciones
                val basicAppsResult = homeUseCase.getAllApps()
                if (basicAppsResult.isSuccess) {
                    val basicApps = basicAppsResult.getOrNull() ?: emptyList()
                    val enrichedApps = mutableListOf<App>()

                    // 2. Para cada aplicación, obtener sus archivos y combinarlos
                    for (basicApp in basicApps) {
                        // Llama a homeUseCase.getAppFiles(appId)
                        val appFilesResult = homeUseCase.getAppFiles(basicApp.id)
                        if (appFilesResult.isSuccess) {
                            val appWithFiles = appFilesResult.getOrNull()
                            if (appWithFiles != null) {
                                val combinedApp = basicApp.copy(
                                    filesDetails = appWithFiles.filesDetails,
                                    uiDetails = AppUIDetails()
                                )
                                enrichedApps.add(combinedApp)
                            } else {
                                enrichedApps.add(basicApp.copy(uiDetails = AppUIDetails()))
                                Log.w("HomeViewModel", "No se encontraron archivos para la app: ${basicApp.name} (${basicApp.id})")
                            }
                        } else {
                            enrichedApps.add(basicApp.copy(uiDetails = AppUIDetails()))
                            Log.e("HomeViewModel", "Error al cargar archivos para la app ${basicApp.name} (${basicApp.id}): ${appFilesResult.exceptionOrNull()?.message}", appFilesResult.exceptionOrNull())
                        }
                    }
                    _appList.value = enrichedApps
                    Log.d("HomeViewModel", "Aplicaciones y sus archivos cargados con éxito: ${_appList.value.size}")
                } else {
                    _appsError.value = basicAppsResult.exceptionOrNull()?.message ?: "Error desconocido al cargar las aplicaciones."
                    Log.e("HomeViewModel", "Error al cargar aplicaciones básicas:", basicAppsResult.exceptionOrNull())
                }
            } catch (e: Exception) {
                _appsError.value = e.message ?: "Error inesperado al cargar las aplicaciones."
                Log.e("HomeViewModel", "Excepción al cargar aplicaciones:", e)
            } finally {
                _appsLoading.value = false
            }
        }
    }

    fun fetchAppFiles(appId: String) {
        viewModelScope.launch {
            _appFilesLoading.value = true
            _appFilesError.value = null
            _selectedAppFiles.value = null

            try {
                // Llama a homeUseCase.getAppFiles(appId)
                val result = homeUseCase.getAppFiles(appId)
                if (result.isSuccess) {
                    val appWithFiles = result.getOrNull()
                    if (appWithFiles != null) {
                        _appList.value = _appList.value.map { app ->
                            if (app.id == appWithFiles.id) {
                                app.copy(
                                    filesDetails = appWithFiles.filesDetails,
                                    uiDetails = AppUIDetails()
                                )
                            } else {
                                app
                            }
                        }
                        _selectedAppFiles.value = _appList.value.find { it.id == appId }
                        Log.d("HomeViewModel", "Archivos de la app $appId cargados con éxito.")
                    } else {
                        _appFilesError.value = "No se encontraron archivos para la aplicación $appId."
                    }
                } else {
                    _appFilesError.value = result.exceptionOrNull()?.message ?: "Error desconocido al cargar los archivos de la aplicación."
                    Log.e("HomeViewModel", "Error al cargar archivos de la app:", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                _appFilesError.value = e.message ?: "Error inesperado al cargar los archivos de la aplicación."
                Log.e("HomeViewModel", "Excepción al cargar archivos de la app:", e)
            } finally {
                _appFilesLoading.value = false
            }
        }
    }
}