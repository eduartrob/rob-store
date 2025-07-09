package com.robstore.core.di

import com.robstore.core.appcontext.AppContextHolder
import com.robstore.core.hardware.data.CameraManager
import com.robstore.core.hardware.domain.CameraRepository

object HardwareModule {
    val cameraManager: CameraRepository by lazy {
        CameraManager(AppContextHolder.get())
    }
}