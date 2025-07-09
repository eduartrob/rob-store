package com.robstore.features.authentication.login.presentation.state

sealed class EmailValidationState {
    object Empty : EmailValidationState()
    object Invalid : EmailValidationState()
    object NotRegistered :  EmailValidationState()
    object Valid : EmailValidationState()
    object Error : EmailValidationState()
}

sealed class PasswordValidatioinState {
    object Invalid: PasswordValidatioinState()
    object Valid: PasswordValidatioinState()
}


sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState() // Mientras la llamada a la API está en curso
    object Success : LoginUiState() // Cuando el login es exitoso
    data class Error(val message: String) : LoginUiState() // Si hay un error
}