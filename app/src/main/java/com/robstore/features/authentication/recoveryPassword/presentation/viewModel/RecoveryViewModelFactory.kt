package com.robstore.features.authentication.recoveryPassword.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.features.authentication.recoveryPassword.domain.useCase.RecoveryUseCase

class RecoveryViewModelFactory(
    private val recoveryUseCase: RecoveryUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun<T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RecoveryPasswdViewModel::class.java)) {
            return RecoveryPasswdViewModel(recoveryUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

}