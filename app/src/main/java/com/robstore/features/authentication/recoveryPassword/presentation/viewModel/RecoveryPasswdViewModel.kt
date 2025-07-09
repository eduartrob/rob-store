package com.robstore.features.authentication.recoveryPassword.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.features.authentication.recoveryPassword.presentation.state.EmailValidationState
import com.robstore.features.authentication.recoveryPassword.domain.useCase.RecoveryUseCase
import com.robstore.features.authentication.recoveryPassword.presentation.state.PasswordValidationState
import com.robstore.features.authentication.recoveryPassword.presentation.state.RecoveryUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class RecoveryPasswdViewModel(
    private val recoveryUseCase: RecoveryUseCase
): ViewModel() {

    private val _emailInputText = MutableStateFlow("")
    val emailInputText: StateFlow<String> = _emailInputText

    private val _emailValidationState = MutableStateFlow<EmailValidationState?>(null)
    val emailValidationState: MutableStateFlow<EmailValidationState?> = _emailValidationState

    private var wasError = false
    private var hasEmailBeenFocused = false

    private val _recoveryUiState = MutableStateFlow<RecoveryUiState>(RecoveryUiState.Idle)
    val recoveryUiState: StateFlow<RecoveryUiState> = _recoveryUiState.asStateFlow()

    // --- NUEVOS ESTADOS para el pop-up de verificación de código ---
    private val _verificationCodeInput = MutableStateFlow("")
    val verificationCodeInput: StateFlow<String> = _verificationCodeInput.asStateFlow()

    private val _remainingSeconds = MutableStateFlow(0)
    val remainingSeconds: StateFlow<Int> = _remainingSeconds.asStateFlow()

    private val _verificationCodeError = MutableStateFlow<String?>(null)
    val verificationCodeError: StateFlow<String?> = _verificationCodeError.asStateFlow()

    private var countdownJob: Job? = null


    // --- NUEVOS ESTADOS para el pop-up de nueva contraseña ---
    private val _newPasswordInput = MutableStateFlow("")
    val newPasswordInput: StateFlow<String> = _newPasswordInput.asStateFlow()

    private val _confirmPasswordInput = MutableStateFlow("")
    val confirmPasswordInput: StateFlow<String> = _confirmPasswordInput.asStateFlow()

    private val _newPasswordValidationState = MutableStateFlow<PasswordValidationState?>(null)
    val newPasswordValidationState: StateFlow<PasswordValidationState?> = _newPasswordValidationState.asStateFlow()

    private val _confirmPasswordValidationState = MutableStateFlow<PasswordValidationState?>(null)
    val confirmPasswordValidationState: StateFlow<PasswordValidationState?> = _confirmPasswordValidationState.asStateFlow()

    private var verifiedCode: Int? = null

    private val _navigateToLoginAfterPasswordUpdate = Channel<Unit>()
    val navigateToLoginAfterPasswordUpdate = _navigateToLoginAfterPasswordUpdate.receiveAsFlow()



    fun onEmailChange(email: String) {
        _emailInputText.value = email

        if (wasError) {
            _emailValidationState.value = EmailValidationState.Valid
            wasError = false
        }
    }
    fun onEmailFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) {
            if (hasEmailBeenFocused) {
                validateEmail(_emailInputText.value)
            } else {
                hasEmailBeenFocused = true
            }
        }
    }
    private fun validateEmail(email: String) {
        _emailValidationState.value = when {
            email.isBlank() -> {
                wasError = true
                EmailValidationState.Empty
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                wasError = true
                EmailValidationState.Invalid
            }
            else -> EmailValidationState.Valid
        }
    }

    fun validateCredentials() {
        val email = emailInputText.value

        if (email.isBlank()) {
            _emailValidationState.value = EmailValidationState.Empty
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailValidationState.value = EmailValidationState.Invalid
            return
        }
        sendRecoveryEmail(email)
    }


    fun sendRecoveryEmail(email: String) {
        viewModelScope.launch {
            _recoveryUiState.value = RecoveryUiState.Loading
            val result =  recoveryUseCase(email)

            result.onSuccess { data ->
                if (data.message.isNotEmpty()) {
                    _emailValidationState.value = EmailValidationState.Valid
                    Log.d(
                        "LoginViewModel", "Envio de codigo exitoso para $email."
                    )
                    _recoveryUiState.value = RecoveryUiState.Success

                    _recoveryUiState.value = RecoveryUiState.CodeSent
                    startCountdownTimer(600)

                } else {
                    _emailValidationState.value = EmailValidationState.NotRegistered
                    Log.d("LoginViewModel", "Usuario no registrado")
                    _recoveryUiState.value = RecoveryUiState.Error("usuario es incorrecto o no existe")
                }
            }.onFailure { exception ->
                _emailValidationState.value = EmailValidationState.NotRegistered
                Log.e("LoginViewModel", "usuario es incorrecto o no existe: ${exception.message}")
                _recoveryUiState.value = RecoveryUiState.Error("usuario es incorrecto o no existe")
            }
        }
    }



    fun onVerificationCodeChange(newCode: String) {
        _verificationCodeInput.value = newCode
        _verificationCodeError.value = null // Limpiar error al escribir
    }

    fun verifyCode() {
        val code = _verificationCodeInput.value
        _verificationCodeError.value = null // Limpiar errores anteriores

        // Validación básica del formato del código (ej. 6 dígitos numéricos)
        if (code.length != 6 || !code.all { it.isDigit() }) {
            _verificationCodeError.value = "Ingresa un código válido de 6 dígitos."
            return
        }

        // Convertir la cadena a Int
        val codeInt = try {
            code.toInt()
        } catch (e: NumberFormatException) {
            _verificationCodeError.value = "El código debe ser numérico."
            return
        }

        viewModelScope.launch {
            _recoveryUiState.value = RecoveryUiState.Loading

            // Llama al caso de uso para verificar el código
            val result = recoveryUseCase.verifyRecoveryCode(codeInt)

            result.onSuccess { data ->
                if (data.message == "code-valid") {
                    _recoveryUiState.value = RecoveryUiState.CodeVerified
                    countdownJob?.cancel()
                    verifiedCode = codeInt
                    Log.e("LoginViewModel", "Code exitoso")
                } else {
                    _recoveryUiState.value = RecoveryUiState.Error(data.message)
                    _verificationCodeError.value = "Código incorrecto o caducado."
                }
            }.onFailure { exception ->
                Log.e("LoginViewModel", "Error en code: ${exception.message}")
            }
        }
    }

    fun resendCode() {
        if (_remainingSeconds.value <= 0 || countdownJob?.isActive == false) {
            sendRecoveryEmail(emailInputText.value)
        } else {
            Log.d("RecoveryViewModel", "No se puede reenviar el código, el temporizador sigue activo.")
        }
    }

    fun cancelVerification() {
        countdownJob?.cancel() // Cancela el contador
        _recoveryUiState.value = RecoveryUiState.Idle // Vuelve al estado inicial de la pantalla de email
        _verificationCodeInput.value = "" // Limpia el campo de código
        _verificationCodeError.value = null // Limpia errores
        _remainingSeconds.value = 0 // Resetea el contador
    }


    private fun startCountdownTimer(initialSeconds: Int) {
        countdownJob?.cancel() // Cancela cualquier trabajo anterior para evitar duplicados
        _remainingSeconds.value = initialSeconds
        countdownJob = viewModelScope.launch {
            while (_remainingSeconds.value > 0) {
                delay(1000) // Espera 1 segundo
                _remainingSeconds.value--
            }
            // Cuando el contador llega a 0, el código ha caducado
            if (_remainingSeconds.value == 0) {
                _verificationCodeError.value = "El código ha caducado. Por favor, reenvía el código."
                _recoveryUiState.value = RecoveryUiState.Error("Código caducado") // O un estado más específico de UI
            }
        }
    }




    // --- Funciones para el pop-up de nueva contraseña ---

    fun onNewPasswordChange(newPassword: String) {
        _newPasswordInput.value = newPassword
        _newPasswordValidationState.value = null
        _confirmPasswordValidationState.value = null
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _confirmPasswordInput.value = confirmPassword
        _confirmPasswordValidationState.value = null
    }

    /**
     * Valida las nuevas contraseñas y las envía para actualización.
     */
    fun updatePassword() {
        val newPass = _newPasswordInput.value
        val confirmPass = _confirmPasswordInput.value
        val currentCode = verifiedCode // Usar el código que ya fue verificado

        // Resetear estados de validación
        _newPasswordValidationState.value = null
        _confirmPasswordValidationState.value = null

        // 1. Validar que las contraseñas no estén vacías y coincidan
        if (newPass.isBlank()) {
            _newPasswordValidationState.value = PasswordValidationState.Invalid // O un estado más específico como Empty
            return
        }
        if (newPass != confirmPass) {
            _confirmPasswordValidationState.value = PasswordValidationState.Mismatch
            return
        }

        // 2. Asegurarse de tener el email y el código
        if (currentCode == null) {
            _recoveryUiState.value = RecoveryUiState.Error("Error interno: Código de verificación no disponibles.")
            return
        }

        viewModelScope.launch {
            _recoveryUiState.value = RecoveryUiState.Loading
            val result = recoveryUseCase.resetPassword(currentCode, newPass)

            result.onSuccess { data ->
                if (data.message == "password-reset-success") {
                    _recoveryUiState.value = RecoveryUiState.NewPassword
                    _recoveryUiState.value = RecoveryUiState.PasswordUpdateSuccess("Contraseña actualizada con éxito. Redirigiendo al login...")


                    _newPasswordInput.value = ""
                    _confirmPasswordInput.value = ""
                    _newPasswordValidationState.value = null
                    _confirmPasswordValidationState.value = null
                } else {
                    _recoveryUiState.value = RecoveryUiState.Error(data.message)
                }
            }.onFailure { exception ->
//                _recoveryUiState.value = RecoveryUiState.Error(
//                    when (exception) {
//                        is retrofit2.HttpException -> {
//                            val errorBodyString = exception.response()?.errorBody()?.string()
//                            if (!errorBodyString.isNullOrEmpty()) {
//                                try {
//                                    val errorResponse = Gson().fromJson(errorBodyString, ErrorResponse::class.java)
//                                    errorResponse.message
//                                } catch (e: Exception) { "Error al procesar la respuesta del servidor." }
//                            } else { "Error de red o del servidor: ${exception.code()}" }
//                        }
//                        else -> "Error de conexión: ${exception.message ?: "Desconocido"}"
//                    }
//                )


                Log.e("LoginViewModel", "usuario es incorrecto o no existe: ${exception.message}")
                _recoveryUiState.value = RecoveryUiState.Error("usuario es incorrecto o no existe")
            }
        }
    }


















    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel() // Asegúrate de cancelar el trabajo cuando el ViewModel se destruya
    }
}