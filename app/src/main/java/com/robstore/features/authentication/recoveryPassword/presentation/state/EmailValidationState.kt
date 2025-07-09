package com.robstore.features.authentication.recoveryPassword.presentation.state

sealed class EmailValidationState {
    object Empty : EmailValidationState()
    object Invalid : EmailValidationState()
    object NotRegistered :  EmailValidationState()
    object Valid : EmailValidationState()
    object Error : EmailValidationState()
}



sealed class PasswordValidationState {
    object Invalid: PasswordValidationState()
    object Valid: PasswordValidationState()
    object Mismatch: PasswordValidationState()
    object Weak: PasswordValidationState()
}



sealed class RecoveryUiState {
    object Idle : RecoveryUiState()
    object Loading : RecoveryUiState()
    object Success : RecoveryUiState()
    data class Error(val message: String) : RecoveryUiState()
    object CodeSent : RecoveryUiState()
    object CodeVerified : RecoveryUiState()
    object NewPassword: RecoveryUiState()

    data class PasswordUpdateSuccess(val message: String) : RecoveryUiState()
}