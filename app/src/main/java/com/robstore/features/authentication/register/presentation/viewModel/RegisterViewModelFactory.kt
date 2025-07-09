package com.robstore.features.authentication.register.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.features.authentication.register.domain.useCase.RegisterUseCase

class RegisterViewModelFactory(
    private val registerUseCase: RegisterUseCase
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun<T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(registerUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}