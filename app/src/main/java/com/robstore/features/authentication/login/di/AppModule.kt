package com.robstore.features.authentication.login.di

import android.content.Context
import com.robstore.app.ui.notifications.AndroidNotificationService
import com.robstore.core.common.notifications.INotificationService
import com.robstore.core.hardware.camera.data.repository.CameraManager
import com.robstore.core.hardware.camera.domain.repository.CameraRepository
import com.robstore.core.sync.internet.data.InternetConnectivityManager
import com.robstore.core.sync.internet.domain.repository.InternetConnectivityRepository
import com.robstore.core.sync.internet.domain.useCase.InternetConnectivityUseCase
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
import com.robstore.core.store.local.dataStore.DataStoreManager
import com.robstore.core.store.local.database.RobDatabase
import com.robstore.core.store.local.database.repository.UserRepository
import com.robstore.features.authentication.login.data.datasource.LoginService


object AppModule {
    private lateinit var dataStoreManagerInstance: DataStoreManager
    private lateinit var tokenRepositoryImplInstance: TokenRepositoryImpl

    private lateinit var cameraRepositoryInstance: CameraRepository

    private lateinit var locationRepositoryInstance: LocationRepository
    private lateinit var locationUseCaseInstance: LocationUseCase

    private lateinit var internetConnectivityRepositoryInstance: InternetConnectivityRepository
    private lateinit var internetConnectivityUseCaseInstance: InternetConnectivityUseCase

    private lateinit var robDatabaseInstance: RobDatabase
    private lateinit var userRepositoryInstance: UserRepository

    private lateinit var notificationServiceInstance: INotificationService




    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            dataStoreManagerInstance = DataStoreManager(context.applicationContext)
            tokenRepositoryImplInstance = TokenRepositoryImpl(dataStoreManagerInstance)

            cameraRepositoryInstance = CameraManager(context.applicationContext)

            locationRepositoryInstance = LocationManager(context.applicationContext)
            locationUseCaseInstance = LocationUseCase(locationRepositoryInstance)

            internetConnectivityRepositoryInstance = InternetConnectivityManager(context.applicationContext)
            internetConnectivityUseCaseInstance = InternetConnectivityUseCase(internetConnectivityRepositoryInstance)

            robDatabaseInstance = RobDatabase.getInstance(context.applicationContext)
            userRepositoryInstance = UserRepository(robDatabaseInstance.userDao())

            notificationServiceInstance = AndroidNotificationService(context.applicationContext)


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

    fun getInternetConnectivityUseCase(): InternetConnectivityUseCase {
        check(::internetConnectivityUseCaseInstance.isInitialized) { "InternetConnectivityUseCase no ha sido inicializado. Llama a AppModule.init() primero." }
        return internetConnectivityUseCaseInstance
    }

    fun getUserRepository(): UserRepository {
        check(::userRepositoryInstance.isInitialized) { "UserRepository no ha sido inicializado. Llama a AppModule.init() primero." }
        return userRepositoryInstance
    }

    fun getNotificationService(): INotificationService {
        check(::notificationServiceInstance.isInitialized) { "NotificationService no ha sido inicializado. Llama a AppModule.init() primero." }
        return notificationServiceInstance
    }

}
