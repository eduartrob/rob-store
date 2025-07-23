package com.robstore.features.myApps.presentation.viewModel

import android.content.Context // Importar Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.core.common.GeneralUiState
import com.robstore.core.utils.ImageUtils
import com.robstore.features.myApps.domain.model.App
import com.robstore.features.myApps.domain.useCase.MyAppsNotificationsUseCase
import com.robstore.features.myApps.domain.useCase.MyAppsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MyAppsViewModel(
    private val myAppsUseCase: MyAppsUseCase,
    private val myAppsNotificationsUseCase: MyAppsNotificationsUseCase,
    private val applicationContext: Context
) : ViewModel() {

    private val _myAppsList = MutableStateFlow<List<App>>(emptyList())
    val myAppsList: StateFlow<List<App>> = _myAppsList.asStateFlow()

    private val _myAppsLoading = MutableStateFlow(false)
    val myAppsLoading: StateFlow<Boolean> = _myAppsLoading.asStateFlow()

    private val _myAppsError = MutableStateFlow<String?>(null)
    val myAppsError: StateFlow<String?> = _myAppsError.asStateFlow()

    private val _selectedMyAppDetails = MutableStateFlow<App?>(null)
    val selectedMyAppDetails: StateFlow<App?> = _selectedMyAppDetails.asStateFlow()

    private val _selectedAppFilesLoading = MutableStateFlow(false)
    val selectedAppFilesLoading: StateFlow<Boolean> = _selectedAppFilesLoading.asStateFlow()

    private val _selectedAppFilesError = MutableStateFlow<String?>(null)
    val selectedAppFilesError: StateFlow<String?> = _selectedAppFilesError.asStateFlow()

    // Nuevo estado para la UI de actualización de la aplicación
    private val _appUpdateUiState = MutableStateFlow<GeneralUiState>(GeneralUiState.Idle)
    val appUpdateUiState: StateFlow<GeneralUiState> = _appUpdateUiState.asStateFlow()

    private val _addUpdateAppUiState = MutableStateFlow<GeneralUiState>(GeneralUiState.Idle)
    val addUpdateAppUiState: StateFlow<GeneralUiState> = _addUpdateAppUiState.asStateFlow()



    fun fetchMyApps() {
        viewModelScope.launch {
            _myAppsLoading.value = true
            _myAppsError.value = null

            try {
                val result = myAppsUseCase.getMyApps() // Llama al caso de uso para obtener mis apps
                if (result.isSuccess) {
                    _myAppsList.value = result.getOrNull() ?: emptyList()
                    Log.d("MyAppsViewModel", "Mis aplicaciones cargadas con éxito: ${_myAppsList.value.size}")
                } else {
                    _myAppsError.value = result.exceptionOrNull()?.message ?: "Error desconocido al cargar mis aplicaciones."
                    Log.e("MyAppsViewModel", "Error al cargar mis aplicaciones:", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                _myAppsError.value = e.message ?: "Error inesperado al cargar mis aplicaciones."
                Log.e("MyAppsViewModel", "Excepción al cargar mis aplicaciones:", e)
            } finally {
                _myAppsLoading.value = false
            }
        }
    }

    fun fetchAppFilesForSelectedApp(appId: String) {
        viewModelScope.launch {
            _selectedAppFilesLoading.value = true
            _selectedAppFilesError.value = null
            _selectedMyAppDetails.value = null // Limpiar el detalle anterior

            try {
                val result = myAppsUseCase.getAppFiles(appId)
                if (result.isSuccess) {
                    _selectedMyAppDetails.value = result.getOrNull()
                    Log.d("MyAppsViewModel", "Archivos para la app $appId cargados en detalle.")
                } else {
                    _selectedAppFilesError.value = result.exceptionOrNull()?.message ?: "Error al cargar los detalles de los archivos de la app."
                    Log.e("MyAppsViewModel", "Error al cargar detalles de archivos para app $appId:", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                _selectedAppFilesError.value = e.message ?: "Error inesperado al cargar detalles de archivos."
                Log.e("MyAppsViewModel", "Excepción al cargar detalles de archivos:", e)
            } finally {
                _selectedAppFilesLoading.value = false
            }
        }
    }

    // Funciones para manejar la edición y eliminación (placeholders)
    fun deleteApp(appId: String) {
        viewModelScope.launch {
            Log.d("MyAppsViewModel", "Solicitud de eliminación para app $appId")
            // Aquí iría la lógica para llamar a un use case de eliminación
            // Por ejemplo: myAppsUseCase.deleteApp(appId)
            // Después de la eliminación exitosa, recargar la lista: fetchMyApps()
        }
    }

    fun updateApp(
        updatedApp: App,
        newIconUri: Uri?,
        newApkUri: Uri?,
        newScreenshotUris: List<Uri>
    ) {
        viewModelScope.launch {
            _appUpdateUiState.value = GeneralUiState.Loading
            _myAppsError.value = null // Limpiar errores previos

            try {
                // Convertir URIs a ByteArrays usando ImageUtils
                val iconBytes = newIconUri?.let { ImageUtils.processImageForUpload(applicationContext, it) }
                val apkBytes = newApkUri?.let { applicationContext.contentResolver.openInputStream(it)?.readBytes() }
                val screenshotBytesList = newScreenshotUris.mapNotNull { uri ->
                    ImageUtils.processImageForUpload(applicationContext, uri)
                }

                val result = myAppsUseCase.updateApp(
                    updatedApp,
                    iconBytes,
                    apkBytes,
                    screenshotBytesList
                )

                if (result.isSuccess) {
                    _appUpdateUiState.value = GeneralUiState.Success // Éxito (puedes definir un SuccessType específico si lo necesitas)
                    fetchMyApps() // Refrescar la lista después de la actualización

                    Log.d("MyAppsViewModel", "Aplicación actualizada con éxito: ${updatedApp.name}")
                } else {
                    _appUpdateUiState.value = GeneralUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido al actualizar la aplicación.")
                    Log.e("MyAppsViewModel", "Error al actualizar la aplicación:", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                _appUpdateUiState.value = GeneralUiState.Error(e.message ?: "Error inesperado al actualizar la aplicación.")
                Log.e("MyAppsViewModel", "Excepción al actualizar la aplicación:", e)
            }
        }
    }


    fun addApp(newApp: App, iconUri: Uri?, apkUri: Uri?, screenshotUris: List<Uri>) {
        viewModelScope.launch {
            _addUpdateAppUiState.value = GeneralUiState.Loading

            try {
                val iconBytes = iconUri?.let { ImageUtils.processImageForUpload(applicationContext, it) }
                val apkBytes = apkUri?.let { applicationContext.contentResolver.openInputStream(it)?.readBytes() }
                val screenshotBytesList = screenshotUris.mapNotNull { ImageUtils.processImageForUpload(applicationContext, it) }

                if (iconBytes == null) {
                    _addUpdateAppUiState.value = GeneralUiState.Error("Error: No se pudo procesar el icono de la aplicación.")
                    return@launch
                }
                if (apkBytes == null) {
                    _addUpdateAppUiState.value = GeneralUiState.Error("Error: No se pudo procesar el archivo APK.")
                    return@launch
                }
                if (screenshotBytesList.isEmpty()) {
                    _addUpdateAppUiState.value = GeneralUiState.Error("Error: No se pudieron procesar las capturas de pantalla.")
                    return@launch
                }

                val result = myAppsUseCase.createApp(newApp, iconBytes, apkBytes, screenshotBytesList)

                if (result.isSuccess) {
                    _addUpdateAppUiState.value = GeneralUiState.Success
                    fetchMyApps()
                    myAppsNotificationsUseCase.showAppAddedOrUpdatedSuccess(newApp.name)
                    Log.d("MyAppsViewModel", "Aplicación añadida con éxito")
                } else {
                    _addUpdateAppUiState.value = GeneralUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido al añadir la aplicación.")
                    Log.e("MyAppsViewModel", "Error al añadir aplicación:", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                _addUpdateAppUiState.value = GeneralUiState.Error(e.message ?: "Error inesperado al añadir la aplicación.")
                Log.e("MyAppsViewModel", "Excepción al añadir aplicación:", e)
            }
        }
    }

}
