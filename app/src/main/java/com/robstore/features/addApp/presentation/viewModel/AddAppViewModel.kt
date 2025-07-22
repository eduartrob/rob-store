package com.robstore.features.addApp.presentation.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.robstore.core.common.AppValidationState
import com.robstore.core.common.GeneralUiState
import com.robstore.features.myApps.domain.model.App
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddAppViewModel: ViewModel() {

    // --- Estados de los campos de texto ---
    private val _nameInputText = MutableStateFlow("")
    val nameInputText: StateFlow<String> = _nameInputText

    private val _descriptionInputText = MutableStateFlow("")
    val descriptionInputText: StateFlow<String> = _descriptionInputText

    private val _versionInputText = MutableStateFlow("")
    val versionInputText: StateFlow<String> = _versionInputText


    // --- Estados de las URIs ---
    private val _selectedIcon = MutableStateFlow<Uri?>(null)
    val selectedIcon: StateFlow<Uri?> = _selectedIcon.asStateFlow()

    private val _selectedApk = MutableStateFlow<Uri?>(null)
    val selectedApk: StateFlow<Uri?> = _selectedApk.asStateFlow()

    private val _selectedScreenshots = MutableStateFlow<List<Uri>>(emptyList())
    val selectedScreenshots: StateFlow<List<Uri>> = _selectedScreenshots.asStateFlow()

    // --- Estados de validación ---
    private val _nameValidationState = MutableStateFlow<AppValidationState?>(null)
    val nameValidationState: MutableStateFlow<AppValidationState?> = _nameValidationState

    private val _descriptionValidationState = MutableStateFlow<AppValidationState?>(null)
    val descriptionValidationState: MutableStateFlow<AppValidationState?> =
        _descriptionValidationState

    private val _versionValidationState = MutableStateFlow<AppValidationState?>(null)
    val versionValidationState: StateFlow<AppValidationState?> =
        _versionValidationState.asStateFlow()

    private val _iconValidationState = MutableStateFlow<AppValidationState?>(null)
    val iconValidationState: StateFlow<AppValidationState?> = _iconValidationState.asStateFlow()

    private val _apkValidationState = MutableStateFlow<AppValidationState?>(null)
    val apkValidationState: StateFlow<AppValidationState?> = _apkValidationState.asStateFlow()

    private val _screenshotsValidationState = MutableStateFlow<AppValidationState?>(null)
    val screenshotsValidationState: StateFlow<AppValidationState?> =
        _screenshotsValidationState.asStateFlow()

    // --- Estado general de la UI para la operación de añadir app ---
    private val _addAppUiState = MutableStateFlow<GeneralUiState>(GeneralUiState.Idle)
    val addAppUiState: StateFlow<GeneralUiState> = _addAppUiState.asStateFlow()

    // --- Constantes de validación ---
    private val MIN_NAME_LENGTH = 7
    private val MAX_NAME_LENGTH = 50
    private val MIN_DESCRIPTION_LENGTH = 10
    private val MAX_DESCRIPTION_LENGTH = 500


    // --- Funciones de cambio de valor para los campos ---
    fun onNameChange(name: String) {
        _nameInputText.value = name
        _nameValidationState.value = null
    }

    fun onDescriptionChange(description: String) {
        _descriptionInputText.value = description
        _descriptionValidationState.value = null
    }

    fun onVersionChange(version: String) {
        _versionInputText.value = version
    }

    // --- Funciones para actualizar las URIs de los archivos ---
    fun onIconSelected(uri: Uri?) {
        _selectedIcon.value = uri
        _iconValidationState.value = null
    }

    fun onApkSelected(uri: Uri?) {
        _selectedApk.value = uri
        _apkValidationState.value = null
    }

    fun onScreenshotsSelected(uris: List<Uri>) {
        _selectedScreenshots.value = _selectedScreenshots.value + uris
        _screenshotsValidationState.value = null
    }

    fun clearScreenshots() {
        _selectedScreenshots.value = emptyList()
        _screenshotsValidationState.value = null
    }


    // --- Funciones de validación individuales ---
    private fun validateName(): Boolean {
        return when {
            _nameInputText.value.isBlank() -> {
                _nameValidationState.value = AppValidationState.Empty
                false
            }
            _nameInputText.value.length < MIN_NAME_LENGTH -> {
                _nameValidationState.value = AppValidationState.TooShort
                false
            }
            _nameInputText.value.length > MAX_NAME_LENGTH -> {
                _nameValidationState.value = AppValidationState.TooLong
                false
            }
            else -> {
                _nameValidationState.value = AppValidationState.Valid
                true
            }
        }
    }

    private fun validateDescription(): Boolean {
        return when {
            _descriptionInputText.value.isBlank() -> {
                _descriptionValidationState.value = AppValidationState.Empty
                false
            }
            _descriptionInputText.value.length < MIN_DESCRIPTION_LENGTH -> {
                _descriptionValidationState.value = AppValidationState.TooShort
                false
            }
            _descriptionInputText.value.length > MAX_DESCRIPTION_LENGTH -> {
                _descriptionValidationState.value = AppValidationState.TooLong
                false
            }
            else -> {
                _descriptionValidationState.value = AppValidationState.Valid
                true
            }
        }
    }

    private fun validateVersion(): Boolean {
        return if (_versionInputText.value.isBlank()) {
            _versionValidationState.value = AppValidationState.Empty
            false
        } else {
            _versionValidationState.value = AppValidationState.Valid
            true
        }
    }

    private fun validateIcon(): Boolean {
        return if (_selectedIcon.value == null) {
            _iconValidationState.value = AppValidationState.NotSelected
            false
        } else {
            _iconValidationState.value = AppValidationState.Valid
            true
        }
    }

    private fun validateApk(): Boolean {
        return if (_selectedApk.value == null) {
            _apkValidationState.value = AppValidationState.NotSelected
            false
        } else {
            _apkValidationState.value = AppValidationState.Valid
            true
        }
    }

    private fun validateScreenshots(): Boolean {
        return if (_selectedScreenshots.value.isEmpty()) {
            _screenshotsValidationState.value = AppValidationState.NotSelected
            false
        } else {
            _screenshotsValidationState.value = AppValidationState.Valid
            true
        }
    }


    // --- Validacion de todos los datos ---
    fun validateAndSaveApp(onSave: (newApp: App, iconUri: Uri?, apkUri: Uri?, screenshotUris: List<Uri>) -> Unit) {
        viewModelScope.launch {
            _addAppUiState.value = GeneralUiState.Loading

            val isNameValid = validateName()
            val isDescriptionValid = validateDescription()
            val isVersionValid = validateVersion()
            val isIconValid = validateIcon()
            val isApkValid = validateApk()
            val isScreenshotsValid = validateScreenshots()

            val allFieldsValid = isNameValid && isDescriptionValid && isVersionValid && isIconValid && isApkValid && isScreenshotsValid

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val currentDate = dateFormat.format(Date())

            if (allFieldsValid) {
                Log.d("AddAppViewModel", "Todos los campos son válidos. Preparando para guardar.")
                val newApp = App(
                    id = null.toString(),
                    name = nameInputText.value,
                    description = descriptionInputText.value,
                    version = versionInputText.value,
                    developerId = "",
                    releaseDate = currentDate,
                    rate = 0.0,
                    filesDetails = null,
                    uiDetails = null,
                )
                onSave(newApp, selectedIcon.value, selectedApk.value, selectedScreenshots.value)
            } else {
                _addAppUiState.value = GeneralUiState.Error("Por favor, corrige los errores en el formulario.")
                Log.d("AddAppViewModel", "Errores de validación en el formulario.")
            }
        }
    }

    fun resetUiState() {
        _addAppUiState.value = GeneralUiState.Idle
    }

    fun resetForm() {
        _nameInputText.value = ""
        _descriptionInputText.value = ""
        _versionInputText.value = ""
        _selectedIcon.value = null
        _selectedApk.value = null
        _selectedScreenshots.value = emptyList()

        _nameValidationState.value = null
        _descriptionValidationState.value = null
        _versionValidationState.value = null
        _iconValidationState.value = null
        _apkValidationState.value = null
        _screenshotsValidationState.value = null

        _addAppUiState.value = GeneralUiState.Idle
    }
}