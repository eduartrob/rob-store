package com.robstore.features.profile.di

import android.content.Context
import android.util.Log
import com.robstore.core.hardware.location.data.repository.LocationManager
import com.robstore.core.hardware.location.domain.repository.LocationRepository
import com.robstore.core.hardware.location.domain.useCase.LocationUseCase
import com.robstore.core.network.RetrofitHelper
import com.robstore.core.store.local.DataStoreManager
import com.robstore.core.store.local.dataStore
import com.robstore.features.profile.data.datasource.ProfileService
import com.robstore.features.profile.data.repository.UpdateUserRepositoryImpl
import com.robstore.features.profile.domain.repository.UpdateUserRepository
import com.robstore.features.profile.domain.useCase.UpdateUserUseCase

object UpdateUserAppModule {
    private val profileService: ProfileService by lazy {
        RetrofitHelper.getProfileService()
    }

    val updateUserRepositoryImpl: UpdateUserRepositoryImpl by lazy {
        UpdateUserRepositoryImpl(profileService)
    }

    val updateUserRepository: UpdateUserRepository by lazy {
        updateUserRepositoryImpl
    }

    val updateUserUseCase: UpdateUserUseCase by lazy {
        UpdateUserUseCase(updateUserRepository)
    }
}