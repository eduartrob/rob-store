package com.robstore.features.authentication.login.presentation.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.features.authentication.login.di.AppModule.loginUseCase
import com.robstore.features.authentication.login.presentation.state.EmailValidationState
import com.robstore.features.authentication.login.presentation.state.PasswordValidatioinState
import com.robstore.features.authentication.login.di.AppModule.tokenRepository
import com.robstore.features.authentication.login.presentation.state.LoginUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch


class LoginViewModel : ViewModel(){

    private val _emailInputText = MutableStateFlow("")
    val emailInputText: StateFlow<String> = _emailInputText

    private val _emailValidationState = MutableStateFlow<EmailValidationState?>(null)
    val emailValidationState: MutableStateFlow<EmailValidationState?> = _emailValidationState

    private var wasError = false
    private var hasEmailBeenFocused = false



    private val _password = MutableStateFlow("")
    val password : StateFlow<String> = _password

    private val _passwordValidationState = MutableStateFlow<PasswordValidatioinState?>(null)
    val passwordValidationState: MutableStateFlow<PasswordValidatioinState?> = _passwordValidationState

    private var wasErrorPassword = false

    private var _error = MutableLiveData<String>("")
    val error : LiveData<String> = _error

    private val _initialDestination = MutableStateFlow<String?>(null)
    val initialDestination: StateFlow<String?> = _initialDestination

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()


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
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> EmailValidationState.Invalid
            else -> EmailValidationState.Valid
        }
    }



    fun onPasswordChange (password : String) {
        _password.value = password

        if(wasErrorPassword){
            _passwordValidationState.value = PasswordValidatioinState.Valid
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
            _passwordValidationState.value = PasswordValidatioinState.Invalid
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailValidationState.value = EmailValidationState.Invalid
            return
        }

        viewModelScope.launch {
            _loginUiState.value = LoginUiState.Loading

            val result = loginUseCase(email, password)

            result.onSuccess { data ->
                if (data.message.isNotEmpty()) {
                    _emailValidationState.value = EmailValidationState.Valid
                    _passwordValidationState.value = PasswordValidatioinState.Valid
                    Log.d(
                        "LoginViewModel", "Login exitoso para $email. Token guardado por el Repositorio."
                    )
                    _loginUiState.value = LoginUiState.Success
                } else {
                    _emailValidationState.value = EmailValidationState.Error
                    _passwordValidationState.value = PasswordValidatioinState.Invalid
                    Log.d("LoginViewModel", "Usuario no registrado o contraseña incorrecta.")
                    _loginUiState.value = LoginUiState.Error("usuario o contraseña incorrecta")
                }
            }.onFailure { exception ->
                _emailValidationState.value = EmailValidationState.Error
                _passwordValidationState.value = PasswordValidatioinState.Invalid
                Log.e("LoginViewModel", "Error en login: ${exception.message}")
                _loginUiState.value = LoginUiState.Error("Error de login")
            }
        }
    }


//    private fun checkAutoLogin() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val token = dataStoreToken.getToken().firstOrNull()
//            if (!token.isNullOrEmpty()) {
//                val result = loginUseCase(token)
//                result.onSuccess { isValid ->
//                    _initialDestination.value = "home"
//                }.onFailure { exception ->
//                    sessionManager.clearToken()
//                    _initialDestination.value = "login"
//                }
//            } else {
//                _initialDestination.value = "login"
//            }
//        }
//    }

//    private fun checkLocalTokenExistence() {
//        viewModelScope.launch(Dispatchers.IO) {
//            val token = tokenRepository.getKey().firstOrNull()
//            if (!token.isNullOrEmpty()) {
//                _initialDestination.value = "home"
//            } else {
//                _initialDestination.value = "login"
//            }
//        }
//    }

}
