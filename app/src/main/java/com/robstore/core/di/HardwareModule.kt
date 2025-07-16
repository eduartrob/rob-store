package com.robstore.core.di

import com.robstore.core.appcontext.AppContextHolder
import com.robstore.core.hardware.camera.data.repository.CameraManager
import com.robstore.core.hardware.camera.domain.repository.CameraRepository
import com.robstore.core.hardware.internet.data.InternetConnectivityManager
import com.robstore.core.hardware.internet.domain.repository.InternetConnectivityRepository
import com.robstore.core.hardware.internet.domain.useCase.InternetConnectivityUseCase
import com.robstore.core.network.RetrofitHelper

object HardwareModule {
    val cameraManager: CameraRepository by lazy {
        CameraManager(AppContextHolder.get())
    }





    private val internetConnectivityManager: InternetConnectivityManager by lazy {
        InternetConnectivityManager(AppContextHolder.get())
    }

    private val internetConnectivityRepository: InternetConnectivityRepository by lazy {
        internetConnectivityManager
    }
    val internetConnectivityUseCase: InternetConnectivityUseCase by lazy {
        InternetConnectivityUseCase(internetConnectivityRepository)
    }


}