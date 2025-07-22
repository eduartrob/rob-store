package com.robstore.features.profile.presentation.viewModel

import android.content.Context
import android.net.Uri
import retrofit2.HttpException
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.core.common.EmailValidationState
import com.robstore.core.common.GeneralUiState
import com.robstore.core.common.NameValidationState
import com.robstore.core.common.PhoneValidationState
import com.robstore.core.store.local.dataStore.DataStoreManager
import com.robstore.core.store.local.dataStore.PreferenceKeys
import com.robstore.core.store.local.database.dao.UserDao
import com.robstore.core.store.local.database.entities.UserEntity
import com.robstore.core.store.local.database.repository.UserRepository
import com.robstore.core.sync.internet.domain.useCase.InternetConnectivityUseCase
import com.robstore.core.utils.ImageUtils
import com.robstore.features.authentication.login.di.AppModule
import com.robstore.features.profile.domain.useCase.UpdateUserUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID


class ProfileViewModel(
    private val updateUserUseCase: UpdateUserUseCase,
    private val dataStoreManager: DataStoreManager,
    private val userRepository: UserRepository,
    private val internetConnectivityUseCase: InternetConnectivityUseCase
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

    private val _photoUri = MutableStateFlow<String?>(null)
    val photoUri: StateFlow<String?> = _photoUri

    private val _regionInputText = MutableStateFlow("") // ¡NUEVO! Estado para la región
    val regionInputText: StateFlow<String> = _regionInputText.asStateFlow()

    private val _navigateToLoginEvent = Channel<Unit>(Channel.BUFFERED)
    val navigateToLoginEvent = _navigateToLoginEvent.receiveAsFlow() // Esto es lo que la UI observará


    init {
        viewModelScope.launch {
            dataStoreManager.getUserInformation().collectLatest { userProfile ->
                _nameInputText.value = userProfile.name ?: ""
                _emailInputText.value = userProfile.email ?: ""
                _phoneInputText.value = userProfile.phone ?: ""
            }
        }

        viewModelScope.launch {
            dataStoreManager.getKey(PreferenceKeys.USER_REGION).collectLatest { region ->
                _regionInputText.value = region.toString()
            }
        }

        viewModelScope.launch {
            dataStoreManager.getKey(PreferenceKeys.USER_PROFILE_PICTURE_URI).collectLatest { urlString ->
                _photoUri.value = urlString // deja como String
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



    suspend fun updateCredentials(){
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

        val isConnected = internetConnectivityUseCase.observeConnectivity().first()

        if (isConnected) {
            Log.d("Home", "¡Hay conexión a Internet!")
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
        } else {
            Log.d("Home", "No hay conexión a Internet.")

            val newUserId = UUID.randomUUID().toString()
            val newUser = UserEntity(
                id = newUserId,
                name = name,
                email = email,
                phone = phone
            )
            userRepository.upsertUser(newUser)
            Log.d("Home", "User guardado localmente exitoso $newUser")
        }
    }

    fun resetProfileUiState() {
        _generalUiState.value = GeneralUiState.Idle // O el estado apropiado para tu pantalla después del éxito
    }


    suspend fun logout(){
        _generalUiState.value = GeneralUiState.Loading


        val tokenErrorCodes = setOf(400, 401, 403, 406)
        val result = updateUserUseCase() // Intenta informar al servidor si el token aún es válido

        result.onSuccess {
            Log.d("ProfileViewModel", "Sesión cerrada exitosamente en el servidor.")
            Log.d("ProfileViewModel", "Datos de sesión locales eliminados.")
        }.onFailure { exception ->
            val httpCode = (exception as? HttpException)?.code()
            if (httpCode != null && httpCode in tokenErrorCodes) {
                Log.d("ProfileViewModel", "Error de token ($httpCode) al cerrar sesión. Sesión considerada cerrada, datos locales ya eliminados.")
            } else {
                val msg = when (exception) {
                    is HttpException -> "Error HTTP ${httpCode ?: "desconocido"}: No se pudo cerrar sesión en el servidor."
                    else -> "Error de conexión o inesperado al intentar cerrar sesión en el servidor: ${exception.message ?: "Desconocido"}"
                }
                Log.e("ProfileViewModel", "Error al cerrar sesión en el servidor: $msg. Datos locales eliminados, navegando al login." )
            }
        }

        dataStoreManager.deleteKey(PreferenceKeys.TOKEN)
        dataStoreManager.clearAll()
        _generalUiState.value = GeneralUiState.Success
    }



    fun onImageSelected(uri: Uri, context: Context) {
        uploadProfilePicture(uri, context)
    }
    fun clearProfileImage() {
        viewModelScope.launch {
            dataStoreManager.deleteKey(PreferenceKeys.USER_PROFILE_PICTURE_URI)
            _photoUri.value = null
        }
    }

    private fun uploadProfilePicture(uri: Uri, context: Context) {
        viewModelScope.launch {
            _generalUiState.value = GeneralUiState.Loading

            val isConnected = internetConnectivityUseCase.observeConnectivity().first()
            if (!isConnected) {
                _generalUiState.value = GeneralUiState.Error("No hay conexión a internet para subir la imagen.")
                return@launch
            }

            val processedImageBytes = ImageUtils.processImageForUpload(context, uri)

            if (processedImageBytes == null) {
                _generalUiState.value = GeneralUiState.Error("Error al procesar la imagen. Inténtalo de nuevo.")
                Log.e("ProfileViewModel", "Error: processedImageBytes es nulo, no se pudo procesar la imagen.")
                return@launch
            }

            try {
                val requestBody = processedImageBytes.toRequestBody("image/jpeg".toMediaTypeOrNull())
                val imagePart = MultipartBody.Part.createFormData("file", "profile_picture.jpg", requestBody)
                updateUserUseCase.uploadProfilePicture(imagePart).fold(
                    onSuccess = { imageProfile ->
                        val url = imageProfile.url
                        if (url != null) {
                            dataStoreManager.saveKey(PreferenceKeys.USER_PROFILE_PICTURE_URI, url)
                            _photoUri.value = url
                        }
                        _generalUiState.value = GeneralUiState.Success
                        Log.d("ProfileViewModel", "Imagen de perfil subida y URL obtenida: ${url}")
                    },
                    onFailure = { e ->
                        _generalUiState.value = GeneralUiState.Error(e.message ?: "Error al subir la foto.")
                        Log.e("ProfileViewModel", "Error al subir imagen: ${e.message}", e)
                    }
                )
            } catch (e: HttpException) {
                val errorMessage = when (e.code()) {
                    413 -> "La imagen es demasiado grande. Por favor, elige una más pequeña."
                    else -> "Error del servidor al subir imagen (${e.code()}): ${e.message()}"
                }
                _generalUiState.value = GeneralUiState.Error(errorMessage)
                Log.e("ProfileViewModel", "HttpException al subir imagen: $errorMessage", e)
            } catch (e: Exception) {
                _generalUiState.value = GeneralUiState.Error("Error inesperado al subir imagen: ${e.message ?: "Desconocido"}")
                Log.e("ProfileViewModel", "Error inesperado al subir imagen:", e)
            }
        }
    }

}