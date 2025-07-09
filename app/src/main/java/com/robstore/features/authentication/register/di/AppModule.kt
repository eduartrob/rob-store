package com.robstore.features.authentication.register.di


import com.robstore.core.network.RetrofitHelper
import com.robstore.features.authentication.register.data.datasource.RegisterService
import com.robstore.features.authentication.register.data.repository.RegisterRepositoryImpl
import com.robstore.features.authentication.register.domain.repository.RegisterRepository
import com.robstore.features.authentication.register.domain.useCase.RegisterUseCase

object RegisterAppModule {


    private val registerService: RegisterService by lazy {
        RetrofitHelper.getRegisterService()
    }

    private val registerRepositoryImpl: RegisterRepositoryImpl by lazy {
        RegisterRepositoryImpl(registerService)
    }

    private val registerRepository: RegisterRepository by lazy {
        registerRepositoryImpl
    }

    val registerUseCase: RegisterUseCase by lazy {
        RegisterUseCase(registerRepository)
    }

}