package com.robstore.features.authentication.login.presentation.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.features.authentication.login.domain.repository.TokenRepository
import com.robstore.features.authentication.login.domain.useCase.LoginUseCase


class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val tokenRepository: TokenRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}