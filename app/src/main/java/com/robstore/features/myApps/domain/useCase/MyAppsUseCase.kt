package com.robstore.features.myApps.domain.useCase

import android.util.Log
import com.robstore.features.myApps.domain.model.App
import com.robstore.features.myApps.domain.model.DeleteApp
import com.robstore.features.myApps.domain.repository.MyAppsRepository

class MyAppsUseCase(
    private val myAppsRepository: MyAppsRepository // Inyecta el repositorio
) {
    suspend fun getMyApps(): Result<List<App>> {
        return myAppsRepository.getMyApps()
    }

    suspend fun getAppFiles(appId: String): Result<App> {
        return myAppsRepository.getAppFiles(appId)
    }

    suspend fun updateApp(
        updatedApp: App,
        iconBytes: ByteArray?,
        apkBytes: ByteArray?,
        screenshotBytesList: List<ByteArray>,
        screenshotsToKeepUrls: List<String>
    ): Result<App> {
        return myAppsRepository.updateApp(updatedApp, iconBytes, apkBytes, screenshotBytesList, screenshotsToKeepUrls)
    }

    suspend fun createApp(
        createApp: App,
        iconBytes: ByteArray?,
        apkBytes: ByteArray?,
        screenshotBytesList: List<ByteArray>
    ): Result<App>{
        return myAppsRepository.createApp(createApp, iconBytes, apkBytes, screenshotBytesList)
    }


    suspend fun deleteApp(idApp: String): Result<DeleteApp>{
        return myAppsRepository.deleteApp(idApp)
    }
}