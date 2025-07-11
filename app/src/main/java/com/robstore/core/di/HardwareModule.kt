package com.robstore.core.di

import com.robstore.core.appcontext.AppContextHolder
import com.robstore.core.hardware.camera.data.repository.CameraManager
import com.robstore.core.hardware.camera.domain.repository.CameraRepository

object HardwareModule {
    val cameraManager: CameraRepository by lazy {
        CameraManager(AppContextHolder.get())
    }
}