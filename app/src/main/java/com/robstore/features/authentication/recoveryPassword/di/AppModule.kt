package com.robstore.features.authentication.recoveryPassword.di

import com.robstore.core.network.RetrofitHelper
import com.robstore.features.authentication.recoveryPassword.data.datasource.RecoveryService
import com.robstore.features.authentication.recoveryPassword.data.repository.RecoveryRepositoryImpl
import com.robstore.features.authentication.recoveryPassword.domain.repository.RecoveryRepository
import com.robstore.features.authentication.recoveryPassword.domain.useCase.RecoveryUseCase

object AppModule {



    private val recoveryService: RecoveryService by lazy {
        RetrofitHelper.getRecoveryService()
    }

    private val recoveryRepositoryImpl: RecoveryRepositoryImpl by lazy {
        RecoveryRepositoryImpl(recoveryService)
    }

    private val recoveryRepository: RecoveryRepository by lazy {
        recoveryRepositoryImpl
    }

    val recoveryUseCase: RecoveryUseCase by lazy {
        RecoveryUseCase(recoveryRepository)
    }
}