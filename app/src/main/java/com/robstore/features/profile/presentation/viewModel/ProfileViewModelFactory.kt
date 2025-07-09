package com.robstore.features.profile.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.core.hardware.domain.CameraRepository
import com.robstore.core.store.local.DataStoreManager
import com.robstore.features.profile.domain.useCase.UpdateUserUseCase

class ProfileViewModelFactory(
    private val updateUserUseCase: UpdateUserUseCase,
    private val dataStoreManager: DataStoreManager,
    private val cameraRepository: CameraRepository
): ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun<T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                updateUserUseCase,
                dataStoreManager,
                cameraRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}