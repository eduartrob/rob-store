package com.robstore.features.profile.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.core.store.local.dataStore.DataStoreManager
import com.robstore.core.store.local.database.repository.UserRepository
import com.robstore.core.sync.internet.domain.useCase.InternetConnectivityUseCase
import com.robstore.features.profile.domain.useCase.UpdateUserUseCase

class ProfileViewModelFactory(
    private val updateUserUseCase: UpdateUserUseCase,
    private val dataStoreManager: DataStoreManager,
    private val userRepository: UserRepository,
    private val internetConnectivityUseCase: InternetConnectivityUseCase
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun<T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                updateUserUseCase,
                dataStoreManager,
                userRepository,
                internetConnectivityUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}


