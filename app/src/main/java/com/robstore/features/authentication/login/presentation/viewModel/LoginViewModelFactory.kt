package com.robstore.features.authentication.login.presentation.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.core.store.local.dataStore.DataStoreManager
import com.robstore.core.sync.internet.domain.useCase.InternetConnectivityUseCase
import com.robstore.features.authentication.login.domain.useCase.LoginUseCase


class LoginViewModelFactory(
    private val loginUseCase: LoginUseCase,
    private val dataStoreManager: DataStoreManager,
    private val internetConnectivityUseCase: InternetConnectivityUseCase
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(loginUseCase, dataStoreManager, internetConnectivityUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}