package com.robstore.features.profile.presentation.viewModel

import android.net.Uri
import retrofit2.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.robstore.core.common.EmailValidationState
import com.robstore.core.common.GeneralUiState
import com.robstore.core.common.NameValidationState
import com.robstore.core.common.PhoneValidationState
import com.robstore.core.hardware.domain.CameraRepository
import com.robstore.core.store.local.DataStoreManager
import com.robstore.core.store.local.PreferenceKeys
import com.robstore.features.profile.domain.useCase.UpdateUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val updateUserUseCase: UpdateUserUseCase,
    private val dataStoreManager: DataStoreManager,
    private val cameraRepository: CameraRepository
): ViewModel() {
    private val _generalUiState = MutableStateFlow<GeneralUiState>(GeneralUiState.Idle)
    val generalUiState: StateFlow<GeneralUiState> = _generalUiState.asStateFlow()


    private val _nameInputText = MutableStateFlow("")
    val nameInputText: StateFlow<String> = _nameInputText

    private val _nameValidationState = MutableStateFlow<NameValidationState?>(null)
    val nameValidationState: MutableStateFlow<NameValidationState?> = _nameValidationState

    private val _emailInputText = MutableStateFlow("")
    val emailInputText: StateFlow<String> = _emailInputText

    private val _emailValidationState = MutableStateFlow<EmailValidationState?>(null)
    val emailValidationState: MutableStateFlow<EmailValidationState?> = _emailValidationState

    private val _phoneInputText = MutableStateFlow("")
    val phoneInputText: StateFlow<String> = _phoneInputText

    private val _phoneValidationState = MutableStateFlow<PhoneValidationState?>(null)
    val phoneValidationState: MutableStateFlow<PhoneValidationState?> = _phoneValidationState

    private val _photoUri = MutableStateFlow<Uri?>(null)
    val photoUri = _photoUri.asStateFlow()

    private var tempCameraUri: Uri? = null



    init {
        viewModelScope.launch {
            dataStoreManager.getUserInformation().collect { userProfileLocal ->
                _nameInputText.value = userProfileLocal.name ?: ""
                _emailInputText.value = userProfileLocal.email ?: ""
                _phoneInputText.value = userProfileLocal.phone ?: ""
                _nameValidationState.value = if (userProfileLocal.name?.isNotEmpty() == true) NameValidationState.Valid else null
                _emailValidationState.value = if (userProfileLocal.email?.isNotEmpty() == true) EmailValidationState.Valid else null
                _phoneValidationState.value = if (userProfileLocal.phone?.isNotEmpty() == true) PhoneValidationState.Valid else null
            }
        }
    }



    fun onNameChange(name: String) {
        _nameInputText.value = name
        _nameValidationState.value = null
    }
    fun onEmailChange(email: String) {
        _emailInputText.value = email
        _emailValidationState.value = null
    }
    fun onPhoneChange(phone: String) {
        _phoneInputText.value = phone
        _phoneValidationState.value = null
    }


    fun onNameFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) {
            if (_nameInputText.value.isNotEmpty()){
                validateNameFormat(_nameInputText.value)
            } else {
                _nameValidationState.value = null
            }
        }
    }
    private fun validateNameFormat(username: String): NameValidationState {
        val minLength = 4
        val maxLength = 20
        val allowedCharactersRegex = Regex("^[a-zA-Z0-9_.]+$")
        return when {
            //username.isBlank() -> UsernameValidationState.Empty
            username.length < minLength -> NameValidationState.TooShort
            username.length > maxLength -> NameValidationState.TooLong
            !username.matches(allowedCharactersRegex) -> NameValidationState.InvalidCharacters
            else -> NameValidationState.Valid
        }
    }

    fun onEmailFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) {
            if (_emailInputText.value.isNotEmpty()){
                validateEmailFormat(_emailInputText.value)
            } else {
                _emailValidationState.value = null
            }
        }
    }
    private fun validateEmailFormat(email: String): EmailValidationState {
        return when {
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailValidationState.Invalid
            else -> EmailValidationState.Valid
        }
    }

    fun onPhoneFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) {
            if (_phoneInputText.value.isNotEmpty()) {
                validatePhoneNumberFormat(_phoneInputText.value)
            } else {
                _phoneValidationState.value = null
            }
        }
    }
    private fun validatePhoneNumberFormat(phone: String): PhoneValidationState {
        val requiredLength = 10
        return when {
            //phone.isBlank() -> PhoneValidationState.Empty
            !phone.all { it.isDigit() } -> PhoneValidationState.InvalidFormat
            phone.length < requiredLength -> PhoneValidationState.TooShort
            phone.length > requiredLength -> PhoneValidationState.TooLong
            else -> PhoneValidationState.Valid
        }
    }

    fun updateCredentials(){
        val name = _nameInputText.value
        val email = _emailInputText.value
        val phone = _phoneInputText.value

        _generalUiState.value = GeneralUiState.Idle

        val nameState = validateNameFormat(name)
        val emailState = validateEmailFormat(email)
        val phoneState = validatePhoneNumberFormat(phone)

        if (nameState !is NameValidationState.Valid ||
            emailState !is EmailValidationState.Valid ||
            phoneState !is PhoneValidationState.Valid) {
            _generalUiState.value = GeneralUiState.Error("Por favor, corrige los errores en el formulario.")
            return
        }


        viewModelScope.launch {
            _generalUiState.value = GeneralUiState.Loading
            val result = updateUserUseCase(name, email, phone)

            result.onSuccess { data ->
                dataStoreManager.saveUserInformation(
                    name = data.name,
                    email = data.email,
                    phone = data.phone
                )
                _generalUiState.value = GeneralUiState.Success
                Log.d("ProfileViewModel", "Perfil actualizado exitosamente: ${data}")
            }.onFailure { exception ->
                val errorMessage = exception.message ?: "Error desconocido al actualizar el perfil."
                _generalUiState.value = GeneralUiState.Error(errorMessage)
                Log.e("ProfileViewModel", "Error al actualizar perfil: $errorMessage", exception)
            }
        }
    }


    fun logout(){
        viewModelScope.launch {
            _generalUiState.value = GeneralUiState.Loading
            val result = updateUserUseCase()
            result.onSuccess {
                dataStoreManager.deleteKey(PreferenceKeys.TOKEN)
                dataStoreManager.clearAll()
                _generalUiState.value = GeneralUiState.Success
                Log.d("ProfileViewModel", "Sesión cerrada exitosamente. Token y datos de usuario eliminados. $result")
            }.onFailure { exception ->
                when (exception) {
                    is HttpException -> {
                        val httpCode = exception.code()
                        when (httpCode) {
                            400 -> {
                                val msg = "Error 400 (Bad Request): Invalid or expired token. Could not log out from server. API Message"
                                Log.e("ProfileViewModel", "UI Error Message (400): $msg")
                            }
                            401 -> {
                                val msg = "Error 401 (Unauthorized): Unauthorized. Invalid or missing token. Could not log out from server. API Message"
                                Log.e("ProfileViewModel", "UI Error Message (401): $msg")
                            }
                            500 -> {
                                val msg = "Error 500 (Internal Server Error): Could not log out from server. API Message"
                                Log.e("ProfileViewModel", "UI Error Message (500): $msg")
                            }
                            550 -> { // Assuming 550 is a custom error code from your API
                                val msg = "Error 550 (Custom Server Error): Custom server error. Could not log out from server. API Message"
                                Log.e("ProfileViewModel", "UI Error Message (550): $msg")
                            }
                            else -> {
                                val msg = "HTTP Error ${exception}: Could not log out from server. API Message"
                                Log.e("ProfileViewModel", "UI Error Message (Generic HTTP): $msg")
                            }
                        }
                    }
                    else -> {
                        val msg = " ${exception.message ?: "Unknown"}"
                        Log.e("ProfileViewModel", "UI Error Message (Connection/Unexpected): $msg")
                    }
                }
                val errorMessage = "Error al cerrar sesión. Por favor, inténtalo de nuevo. Detalles en logs."
                _generalUiState.value = GeneralUiState.Error(errorMessage)
            }
        }
    }



    fun prepareCameraCapture(): Uri? {
        tempCameraUri = cameraRepository.createImageUriForCamera()
        return tempCameraUri
    }

    fun onPhotoCaptured(success: Boolean) {
        tempCameraUri?.let { uri ->
            if (success) {
                viewModelScope.launch {
                    val result = cameraRepository.processCapturedPhoto(uri)
                    if (result.isSuccess) {
                        _photoUri.value = result.getOrNull()
                    }
                }
            }
        }
    }

    fun onImageSelected(uri: Uri) {
        _photoUri.value = uri
    }

    fun clearCapturedImage(){
        _photoUri.value = null
    }

}