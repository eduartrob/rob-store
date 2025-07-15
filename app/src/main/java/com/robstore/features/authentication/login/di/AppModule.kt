package com.robstore.features.authentication.login.di

import android.content.Context
import com.robstore.core.hardware.camera.data.repository.CameraManager
import com.robstore.core.hardware.camera.domain.repository.CameraRepository
import com.robstore.core.hardware.location.data.repository.LocationManager
import com.robstore.core.hardware.location.domain.repository.LocationRepository
import com.robstore.core.hardware.location.domain.useCase.LocationUseCase
import com.robstore.features.authentication.login.data.repository.LoginRepositoryImpl
import com.robstore.features.authentication.login.data.repository.TokenRepositoryImpl
import com.robstore.features.authentication.login.domain.repository.LoginRepository
import com.robstore.features.authentication.login.domain.repository.TokenRepository
import com.robstore.features.authentication.login.domain.useCase.LoginUseCase
import com.robstore.core.network.RetrofitHelper
import com.robstore.core.network.interceptor.AddTokenInterceptor
import com.robstore.core.network.interceptor.TokenCaptureInterceptor
import com.robstore.core.network.interceptor.provideLoggingInterceptor
import com.robstore.core.store.local.DataStoreManager
import com.robstore.features.authentication.login.data.datasource.LoginService


object AppModule {
    private lateinit var dataStoreManagerInstance: DataStoreManager
    private lateinit var tokenRepositoryImplInstance: TokenRepositoryImpl

    private lateinit var cameraRepositoryInstance: CameraRepository

    private lateinit var locationRepositoryInstance: LocationRepository
    private lateinit var locationUseCaseInstance: LocationUseCase

    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            dataStoreManagerInstance = DataStoreManager(context.applicationContext)
            tokenRepositoryImplInstance = TokenRepositoryImpl(dataStoreManagerInstance)

            cameraRepositoryInstance = CameraManager(context.applicationContext)

            locationRepositoryInstance = LocationManager(context.applicationContext)
            locationUseCaseInstance = LocationUseCase(locationRepositoryInstance)

            val allInterceptors = listOf(
                TokenCaptureInterceptor(dataStoreManagerInstance),
                AddTokenInterceptor(dataStoreManagerInstance),
                provideLoggingInterceptor()
            )
            RetrofitHelper.init(
                dataStoreManager = dataStoreManagerInstance,
                extraInterceptors = allInterceptors
            )
            isInitialized = true
        }
    }

    private val loginService: LoginService by lazy {
        RetrofitHelper.getLoginService()
    }

    private val loginRepositoryImpl: LoginRepositoryImpl by lazy {
        LoginRepositoryImpl(loginService)
    }


    val tokenRepository: TokenRepository by lazy {
        TokenRepositoryImpl(dataStoreManagerInstance)
    }

    private val loginRepository: LoginRepository by lazy {
        loginRepositoryImpl
    }

    val loginUseCase: LoginUseCase by lazy {
        LoginUseCase(loginRepository)
    }

    fun getDataStoreManager(): DataStoreManager {
        check(::dataStoreManagerInstance.isInitialized) { "DataStoreManager no ha sido inicializado. Llama a AppModule.init() primero." }
        return dataStoreManagerInstance
    }

    fun getCameraRepository(): CameraRepository {
        check(::cameraRepositoryInstance.isInitialized) { "CameraRepository no ha sido inicializado. Llama a AppModule.init() primero." }
        return cameraRepositoryInstance
    }

    fun getLocationUseCase(): LocationUseCase {
        check(::locationUseCaseInstance.isInitialized) { "LocationUseCase no ha sido inicializado. Llama a UpdateUserAppModule.init() primero." }
        return locationUseCaseInstance
    }
}
