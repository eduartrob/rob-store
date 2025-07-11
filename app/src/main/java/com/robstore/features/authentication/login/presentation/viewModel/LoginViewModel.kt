package com.robstore.features.authentication.login.presentation.viewModel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.core.common.EmailValidationState
import com.robstore.core.common.GeneralUiState
import com.robstore.core.common.PasswordValidationState
import com.robstore.core.store.local.DataStoreManager
import com.robstore.features.authentication.login.di.AppModule.loginUseCase
import com.robstore.features.authentication.login.di.AppModule.tokenRepository
import com.robstore.features.authentication.login.domain.useCase.LoginUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val dataStoreManager: DataStoreManager
    ): ViewModel(){

    private val _emailInputText = MutableStateFlow("")
    val emailInputText: StateFlow<String> = _emailInputText

    private val _emailValidationState = MutableStateFlow<EmailValidationState?>(null)
    val emailValidationState: MutableStateFlow<EmailValidationState?> = _emailValidationState

    private var wasError = false
    private var hasEmailBeenFocused = false


    private val _password = MutableStateFlow("")
    val password : StateFlow<String> = _password

    private val _passwordValidationState = MutableStateFlow<PasswordValidationState?>(null)
    val passwordValidationState: MutableStateFlow<PasswordValidationState?> = _passwordValidationState

    private var wasErrorPassword = false

    private var _error = MutableLiveData<String>("")
    val error : LiveData<String> = _error

    private val _initialDestination = MutableStateFlow<String?>(null)
    val initialDestination: StateFlow<String?> = _initialDestination

    private val _loginUiState = MutableStateFlow<GeneralUiState>(GeneralUiState.Idle)
    val loginUiState: StateFlow<GeneralUiState> = _loginUiState.asStateFlow()


    init {
        viewModelScope.launch {
            tokenRepository.getKey().collectLatest { token ->
                _initialDestination.value = if (token.isNullOrEmpty()) "login" else "home"
                Log.d("LoginViewModel", "Token cambió: ${if (token.isNullOrEmpty()) "NULO/VACÍO" else "EXISTE"}. Destino Inicial: ${_initialDestination.value}")
            }
        }
    }


    fun onEmailChange(email: String) {
        _emailInputText.value = email
        _emailValidationState.value = null
    }

    fun onEmailFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) {
            if (_emailInputText.value.isNotEmpty()) { // Y no está vacío
                validateEmailFormat(_emailInputText.value) // Valida el formato
            } else {
                // Si está vacío y pierde el foco, no mostramos un error de "vacío" aquí.
                // El error de "vacío" se mostrará al intentar iniciar sesión.
                _emailValidationState.value = null // Asegurarse de que no haya un error anterior persistente
            }
        }
    }
    private fun validateEmailFormat(email: String) {
        _emailValidationState.value = when {
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailValidationState.Invalid
            else -> EmailValidationState.Valid
        }
    }



    fun onPasswordChange (password : String) {
        _password.value = password

        if(wasErrorPassword){
            _passwordValidationState.value = PasswordValidationState.Valid
            wasErrorPassword = false
        }
    }



    fun validateCredentials() {
        val email = emailInputText.value
        val password = password.value

        _emailValidationState.value = null
        _passwordValidationState.value = null

        if (email.isBlank() || password.isBlank()) {
            _emailValidationState.value = EmailValidationState.Empty
            _passwordValidationState.value = PasswordValidationState.Invalid
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailValidationState.value = EmailValidationState.Invalid
            return
        }

        viewModelScope.launch {
            _loginUiState.value = GeneralUiState.Loading
            val result = loginUseCase(email, password)

            result.onSuccess { data ->
                dataStoreManager.saveUserInformation(
                    name = data.name,
                    email = data.email,
                    phone = data.phone,
                )

                _emailValidationState.value = EmailValidationState.Valid
                _passwordValidationState.value = PasswordValidationState.Valid
                Log.d(
                    "LoginViewModel", "Login exitoso para $email. Token guardado por el Repositorio."
                )
                _loginUiState.value = GeneralUiState.Success
            }.onFailure { exception ->
                _emailValidationState.value = EmailValidationState.Error
                _passwordValidationState.value = PasswordValidationState.Invalid
                Log.e("LoginViewModel", "Error en login: ${exception.message}")
                _loginUiState.value = GeneralUiState.Error("Error de login: ${exception.message}")
            }
        }
    }
}
