package com.robstore.core.common

sealed class NameValidationState{
    object Valid: NameValidationState()
    object Empty: NameValidationState()
    object TooShort: NameValidationState()
    object TooLong: NameValidationState()
    object InvalidCharacters: NameValidationState()
    object AlreadyTaken: NameValidationState()
    object Error : NameValidationState()
}


sealed class EmailValidationState {
    object Valid : EmailValidationState()
    object Empty : EmailValidationState()
    object NotRegistered :  EmailValidationState()
    object Invalid : EmailValidationState()
    object Error : EmailValidationState()
}

sealed class PasswordValidationState {
    object Valid: PasswordValidationState()
    object Empty: PasswordValidationState()
    object TooShort: PasswordValidationState()
    object Mismatch: PasswordValidationState()
    object Invalid: PasswordValidationState()
    object Error: PasswordValidationState()
}

sealed class PhoneValidationState {
    object Valid : PhoneValidationState()
    object Empty : PhoneValidationState()
    object InvalidFormat : PhoneValidationState()
    object TooShort : PhoneValidationState()
    object TooLong : PhoneValidationState()
    object Error: PhoneValidationState()
}

sealed class AppValidationState {
    object Valid : AppValidationState()
    object Empty : AppValidationState()
    object InvalidFormat : AppValidationState()
    object TooShort : AppValidationState()
    object TooLong : AppValidationState()
    object TooMany : AppValidationState()
    object NotSelected: AppValidationState()
    object Error : AppValidationState()
}


sealed class GeneralUiState {
    object Idle : GeneralUiState()
    object Loading : GeneralUiState()
    object Success : GeneralUiState()
    data class Error(val message: String) : GeneralUiState()
}