package com.robstore.features.authentication.register.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robstore.core.common.EmailValidationState
import com.robstore.features.authentication.register.domain.useCase.RegisterUseCase
import com.robstore.core.common.GeneralUiState
import com.robstore.core.common.PasswordValidationState
import com.robstore.core.common.NameValidationState
import com.robstore.core.common.PhoneValidationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
): ViewModel(){
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


    private val _passwordInputText = MutableStateFlow("")
    val passwordInputText: StateFlow<String> = _passwordInputText

    private val _passwordValidationState = MutableStateFlow<PasswordValidationState?>(null)
    val passwordValidationState: MutableStateFlow<PasswordValidationState?> = _passwordValidationState


    private val _confirmPasswordInputText = MutableStateFlow("")
    val confirmPasswordInputText: StateFlow<String> = _confirmPasswordInputText

    private val _confirmPasswordValidationState = MutableStateFlow<PasswordValidationState?>(null)
    val confirmPasswordValidationState: MutableStateFlow<PasswordValidationState?> = _confirmPasswordValidationState


    private val _phoneInputText = MutableStateFlow("")
    val phoneInputText: StateFlow<String> = _phoneInputText

    private val _phoneValidationState = MutableStateFlow<PhoneValidationState?>(null)
    val phoneValidationState: MutableStateFlow<PhoneValidationState?> = _phoneValidationState



    fun onNameChange(name: String) {
        _nameInputText.value = name
        _nameValidationState.value = null
    }
    fun onEmailChange(email: String) {
        _emailInputText.value = email
        _emailValidationState.value = null
    }
    fun onPasswordChange(password: String) {
        _passwordInputText.value = password
        _passwordValidationState.value = null
    }
    fun onConfirmPasswordChange(confirmPassword: String) {
        _confirmPasswordInputText.value = confirmPassword
        _confirmPasswordValidationState.value = null
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

    fun onPasswordFocusChanged(hasFocus: Boolean) {
        if (!hasFocus) {
            if (_passwordInputText.value.isNotEmpty()){
                validatePasswordFormat(_passwordInputText.value)
            } else {
                _passwordValidationState.value = null
            }
        }
    }
    private fun validatePasswordFormat(password: String): PasswordValidationState {
        val minLength = 8
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
        return when {
            //password.isBlank() -> PasswordValidationState.Empty
            password.length < minLength -> PasswordValidationState.TooShort
            !hasUppercase || !hasLowercase || !hasDigit || !hasSpecialChar -> PasswordValidationState.Invalid
            else -> PasswordValidationState.Valid
        }
    }

    fun onConfirmPasswordFocusChanged(hasFocus: Boolean) {
        val password = _passwordInputText.value
        if (!hasFocus) {
            if (_confirmPasswordInputText.value.isNotEmpty()){
                validateConfirmPasswordFormat(password, _confirmPasswordInputText.value, )
            } else {
                _confirmPasswordValidationState.value = null
            }
        }
    }
    private fun validateConfirmPasswordFormat(originalPassword: String, confirmPassword: String): PasswordValidationState {
        return when {
            //confirmPassword.isBlank() -> PasswordValidationState.Empty
            confirmPassword != originalPassword -> PasswordValidationState.Mismatch // Si no coinciden
            else -> PasswordValidationState.Valid
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


    fun validateCredentials(){
        val name = _nameInputText.value
        val email = _emailInputText.value
        val password = _passwordInputText.value
        val phone = _phoneInputText.value

        _nameValidationState.value = null
        _emailValidationState.value = null
        _passwordValidationState.value = null
        _phoneValidationState.value = null


        if (name.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank()) {
            _nameValidationState.value = NameValidationState.Empty
            _emailValidationState.value = EmailValidationState.Empty
            _passwordValidationState.value = PasswordValidationState.Empty
            _phoneValidationState.value = PhoneValidationState.Empty
            return
        }

        viewModelScope.launch {
            _generalUiState.value = GeneralUiState.Loading
            val result = registerUseCase(name, email, password, phone)

            result.onSuccess { data ->
                if (data.message.isNotEmpty()) {
                    _nameValidationState.value = NameValidationState.Valid
                    _emailValidationState.value = EmailValidationState.Valid
                    _passwordValidationState.value = PasswordValidationState.Valid
                    _confirmPasswordValidationState.value = PasswordValidationState.Valid
                    _phoneValidationState.value = PhoneValidationState.Valid
                    Log.d(
                        "LoginViewModel", "Registro y Login exitoso para $email. Token guardado por el Repositorio."
                    )
                    _generalUiState.value = GeneralUiState.Success
                }
//                else {
//                    _emailValidationState.value = EmailValidationState.Error
//                    _passwordValidationState.value = PasswordValidatioinState.Invalid
//                    Log.d("LoginViewModel", "Usuario no registrado o contraseña incorrecta.")
//                    _loginUiState.value = LoginUiState.Error("usuario o contraseña incorrecta")
//                }
            }.onFailure { exception ->
                _emailValidationState.value = EmailValidationState.Error
                _passwordValidationState.value = PasswordValidationState.Invalid
                Log.e("LoginViewModel", "Error en login: ${exception.message}")
                _generalUiState.value = GeneralUiState.Error("Error de login")
            }
        }
    }
}