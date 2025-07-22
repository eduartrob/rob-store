package com.robstore.features.myApps.domain.repository

import com.robstore.features.myApps.domain.model.App


interface MyAppsRepository {
    suspend fun getMyApps(): Result<List<App>>
    suspend fun getAppFiles(appId: String): Result<App>
    suspend fun updateApp(
        updatedApp: App, // El objeto App con los metadatos actualizados
        iconBytes: ByteArray?, // Los bytes del nuevo icono (opcional)
        apkBytes: ByteArray?, // Los bytes del nuevo APK (opcional)
        screenshotBytesList: List<ByteArray> // La lista de bytes de las nuevas capturas (opcional)
    ): Result<App>
    suspend fun createApp(
        updatedApp: App, // El objeto App con los metadatos actualizados
        iconBytes: ByteArray?, // Los bytes del nuevo icono (opcional)
        apkBytes: ByteArray?, // Los bytes del nuevo APK (opcional)
        screenshotBytesList: List<ByteArray> // La lista de bytes de las nuevas capturas (opcional)
    ): Result<App>
}