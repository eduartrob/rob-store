package com.robstore.features.myApps.domain.repository

import com.robstore.features.myApps.domain.model.App


interface MyAppsRepository {
    suspend fun getMyApps(): Result<List<App>>
    suspend fun getAppFiles(appId: String): Result<App>
    suspend fun updateApp(
        updatedApp: App,
        iconBytes: ByteArray?,
        apkBytes: ByteArray?,
        screenshotBytesList: List<ByteArray>,
        screenshotsToKeepUrls: List<String>
    ): Result<App>
    suspend fun createApp(
        updatedApp: App,
        iconBytes: ByteArray?,
        apkBytes: ByteArray?,
        screenshotBytesList: List<ByteArray>
    ): Result<App>
}