package com.robstore.features.myApps.presentation.viewModel

import android.content.Context // Importar Context
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
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
    private val _appUiState = MutableStateFlow<GeneralUiState>(GeneralUiState.Idle)
    val appUiState: StateFlow<GeneralUiState> = _appUiState.asStateFlow()


    fun resetAppUiState() {
        _appUiState.value = GeneralUiState.Idle
    }

    fun fetchMyApps() {
        viewModelScope.launch {
            _myAppsLoading.value = true
            _myAppsError.value = null

            try {
                val result = myAppsUseCase.getMyApps()
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
            _selectedMyAppDetails.value = null

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

    fun deleteApp(app: App) {
        viewModelScope.launch {
            _appUiState.value = GeneralUiState.Loading
            Log.d("MyAppsViewModel", "Solicitud de eliminación para app ${app.name}")

            myAppsUseCase.deleteApp(app.id)
                .onSuccess { deleteAppResult ->
                    Log.d("MyAppsViewModel", "App ${app.name} eliminada exitosamente. Mensaje: ${deleteAppResult.message}")
                    _appUiState.value = GeneralUiState.Success
                    fetchMyApps()
                    myAppsNotificationsUseCase.showAppDeleteSuccess(app.name)
                }
                .onFailure { exception ->
                    val errorMessage = exception.message ?: "Error desconocido al eliminar la aplicación."
                    Log.e("MyAppsViewModel", "Error al eliminar la app ${app.name}: $errorMessage", exception)
                    _appUiState.value = GeneralUiState.Error(errorMessage)
                }
        }
    }

    fun updateApp(
        updatedApp: App,
        newIconUri: Uri?,
        newApkUri: Uri?,
        newScreenshotUris: List<Uri>
    ) {
        viewModelScope.launch {
            _appUiState.value = GeneralUiState.Loading

            try {
                val originalIconUrl = updatedApp.filesDetails?.iconUrl
                val originalApkUrl = updatedApp.filesDetails?.appFileUrl
                val originalScreenshotUrls = updatedApp.filesDetails?.screenshots ?: emptyList()

                val originalIconUri = originalIconUrl?.toUri()
                val originalApkUri = originalApkUrl?.toUri()
                val originalScreenshotUris = originalScreenshotUrls.map { it.toUri() }


                val iconBytes = if (newIconUri != null && (newIconUri.scheme == "content" || newIconUri.scheme == "file")) {
                    if (originalIconUri == null || !compareUrisIgnoringQueryParams(newIconUri, originalIconUri)) {
                        Log.d("MyAppsViewModel", "Icono local seleccionado/cambiado. Procesando nueva imagen para subir.")
                        ImageUtils.processImageForUpload(applicationContext, newIconUri)
                    } else {
                        Log.d("MyAppsViewModel", "Icono local igual al original (ignorando query params). No se procesa para subir.")
                        null
                    }
                } else if (newIconUri == null && originalIconUri != null) {
                    Log.d("MyAppsViewModel", "Icono existente eliminado. No se procesan bytes para subir.")
                    null
                } else {
                    Log.d("MyAppsViewModel", "Icono no cambiado o ya es una URI remota. No se procesa la imagen para subir.")
                    null
                }

                val apkBytes = if (newApkUri != null && (newApkUri.scheme == "content" || newApkUri.scheme == "file")) {
                    if (originalApkUri == null || !compareUrisIgnoringQueryParams(newApkUri, originalApkUri)) {
                        Log.d("MyAppsViewModel", "APK local seleccionado/cambiado. Procesando nuevo APK para subir.")
                        newApkUri.let { applicationContext.contentResolver.openInputStream(it)?.readBytes() }
                    } else {
                        Log.d("MyAppsViewModel", "APK local igual al original (ignorando query params). No se procesa para subir.")
                        null
                    }
                } else if (newApkUri == null && originalApkUri != null) {
                    Log.d("MyAppsViewModel", "APK existente eliminado. No se procesan bytes para subir.")
                    null
                } else {
                    Log.d("MyAppsViewModel", "APK no cambiado o ya es una URI remota. No se procesa el APK para subir.")
                    null
                }

                // --- Procesamiento de Capturas de Pantalla ---
                val screenshotBytesList = mutableListOf<ByteArray>()
                val finalScreenshotsToKeepUrls = mutableListOf<String>()

                // Filtrar las URIs de capturas de pantalla entrantes
                val newLocalScreenshotUris = newScreenshotUris.filter { it.scheme == "content" || it.scheme == "file" }
                val currentRemoteScreenshotUris = newScreenshotUris.filter { it.scheme == "https" } // Estas son las remotas que persisten en el formulario

                // 1. Añadir las capturas de pantalla remotas que aún están presentes en el formulario
                for (remoteUri in currentRemoteScreenshotUris) {
                    // Solo añadir si realmente estaban entre las originales, o si es una URL "nueva" que ya existe remotamente
                    // Simplificamos: si es una URI https, la consideramos a mantener.
                    finalScreenshotsToKeepUrls.add(remoteUri.toString())
                }

                // 2. Procesar las nuevas capturas de pantalla locales para subir
                if (newLocalScreenshotUris.isNotEmpty()) {
                    Log.d("MyAppsViewModel", "Detectadas ${newLocalScreenshotUris.size} nuevas capturas de pantalla locales. Procesando para subir.")
                    newLocalScreenshotUris.mapNotNullTo(screenshotBytesList) { uri ->
                        ImageUtils.processImageForUpload(applicationContext, uri)
                    }
                } else {
                    Log.d("MyAppsViewModel", "No hay nuevas capturas de pantalla locales seleccionadas.")
                }

                // Comparar las listas originales y las nuevas para el log
//                val sortedOriginalScreenshotPaths = originalScreenshotUris.map { it.pathSegments.lastOrNull() }.sorted()
//                val sortedNewScreenshotPaths = newScreenshotUris.map { it.pathSegments.lastOrNull() }.sorted()

                val hasScreenshotChanges = !compareUriListsIgnoringQueryParams(newScreenshotUris, originalScreenshotUris)
                if (hasScreenshotChanges) {
                    Log.d("MyAppsViewModel", "¡Las capturas de pantalla HAN CAMBIADO!")
                    Log.d("MyAppsViewModel", "Capturas Originales: ${originalScreenshotUris.joinToString { it.lastPathSegment ?: "" }}")
                    Log.d("MyAppsViewModel", "Capturas Nuevas (en formulario): ${newScreenshotUris.joinToString { it.lastPathSegment ?: "" }}")
                } else {
                    Log.d("MyAppsViewModel", "Las capturas de pantalla NO han cambiado.")
                }


                val result = myAppsUseCase.updateApp(
                    updatedApp,
                    iconBytes,
                    apkBytes,
                    screenshotBytesList,
                    screenshotsToKeepUrls = finalScreenshotsToKeepUrls
                )

                if (result.isSuccess) {
                    _appUiState.value = GeneralUiState.Success
                    myAppsNotificationsUseCase.showAppAddedOrUpdatedSuccess(updatedApp.name)
                    fetchMyApps()
                    Log.d("MyAppsViewModel", "Aplicación actualizada con éxito: ${updatedApp.name}")
                } else {
                    _appUiState.value = GeneralUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido al actualizar la aplicación.")
                    Log.e("MyAppsViewModel", "Error al actualizar la aplicación:", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                _appUiState.value = GeneralUiState.Error(e.message ?: "Error inesperado al actualizar la aplicación.")
                Log.e("MyAppsViewModel", "Excepción al actualizar la aplicación:", e)
            }
        }
    }


    fun addApp(newApp: App, iconUri: Uri?, apkUri: Uri?, screenshotUris: List<Uri>) {
        viewModelScope.launch {
            _appUiState.value = GeneralUiState.Loading

            try {
                val iconBytes = iconUri?.let { ImageUtils.processImageForUpload(applicationContext, it) }
                val apkBytes = apkUri?.let { applicationContext.contentResolver.openInputStream(it)?.readBytes() }
                val screenshotBytesList = screenshotUris.mapNotNull { ImageUtils.processImageForUpload(applicationContext, it) }

                if (iconBytes == null) {
                    _appUiState.value = GeneralUiState.Error("Error: No se pudo procesar el icono de la aplicación.")
                    return@launch
                }
                if (apkBytes == null) {
                    _appUiState.value = GeneralUiState.Error("Error: No se pudo procesar el archivo APK.")
                    return@launch
                }
                if (screenshotBytesList.isEmpty()) {
                    _appUiState.value = GeneralUiState.Error("Error: No se pudieron procesar las capturas de pantalla.")
                    return@launch
                }

                val result = myAppsUseCase.createApp(newApp, iconBytes, apkBytes, screenshotBytesList)

                if (result.isSuccess) {
                    _appUiState.value = GeneralUiState.Success
                    myAppsNotificationsUseCase.showAppAddSuccess(newApp.name)
                    fetchMyApps()
                    Log.d("MyAppsViewModel", "Aplicación añadida con éxito")
                } else {
                    _appUiState.value = GeneralUiState.Error(result.exceptionOrNull()?.message ?: "Error desconocido al añadir la aplicación.")
                    Log.e("MyAppsViewModel", "Error al añadir aplicación:", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                _appUiState.value = GeneralUiState.Error(e.message ?: "Error inesperado al añadir la aplicación.")
                Log.e("MyAppsViewModel", "Excepción al añadir aplicación:", e)
            }
        }
    }




    private fun compareUrisIgnoringQueryParams(uri1: Uri, uri2: Uri): Boolean {
        return uri1.scheme == uri2.scheme &&
                uri1.authority == uri2.authority &&
                uri1.path == uri2.path
    }
    private fun compareUriListsIgnoringQueryParams(list1: List<Uri>, list2: List<Uri>): Boolean {
        if (list1.size != list2.size) return false

        // Convertir las URIs a un formato comparable (sin query params) y ordenar para comparar.
        val comparableList1 = list1.map { it.schemeSpecificPart.split("?")[0] }.sorted()
        val comparableList2 = list2.map { it.schemeSpecificPart.split("?")[0] }.sorted()

        return comparableList1 == comparableList2
    }
}
