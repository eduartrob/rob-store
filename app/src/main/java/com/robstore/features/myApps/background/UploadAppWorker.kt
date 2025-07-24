//package com.robstore.features.myApps.background
//
//
//import android.content.Context
//import android.net.Uri
//import android.util.Log
//import androidx.work.CoroutineWorker
//import androidx.work.WorkerParameters
//import androidx.work.workDataOf
//import com.robstore.core.common.notifications.INotificationService
//import com.robstore.features.myApps.domain.useCase.MyAppsUseCase
//import com.robstore.features.myApps.domain.model.App
//import com.robstore.core.utils.ImageUtils
//import com.robstore.features.authentication.login.di.AppModule
//import com.robstore.features.myApps.di.MyAppsModule
//import com.robstore.features.myApps.domain.useCase.MyAppsNotificationsUseCase
//
//// Importa las constantes que defines en features/myApps/background/Constants.kt
//import com.robstore.features.myApps.background.KEY_APP_NAME
//import com.robstore.features.myApps.background.KEY_APP_DESCRIPTION
//import com.robstore.features.myApps.background.KEY_APP_CATEGORY
//import com.robstore.features.myApps.background.KEY_APP_PRICE
//import com.robstore.features.myApps.background.KEY_APP_VERSION
//import com.robstore.features.myApps.background.KEY_ICON_URI_STRING
//import com.robstore.features.myApps.background.KEY_APK_URI_STRING
//import com.robstore.features.myApps.background.KEY_SCREENSHOT_URIS_STRING_ARRAY
//
//
//class UploadAppWorker(
//    appContext: Context,
//    workerParams: WorkerParameters
//) : CoroutineWorker(appContext, workerParams) {
//
//    // Se inicializan de forma perezosa ya que el Worker se creará
//    // por WorkManager, y las dependencias ya deben estar disponibles a través de AppModule/MyAppsModule
//    private val myAppsUseCase: MyAppsUseCase by lazy {
//        MyAppsModule.myAppsUseCase
//    }
//
//    private val notificationService: INotificationService by lazy {
//        AppModule.getNotificationService()
//    }
//
//    private val myAppsNotificationsUseCase: MyAppsNotificationsUseCase by lazy {
//        MyAppsNotificationsUseCase(notificationService)
//    }
//
//    override suspend fun doWork(): Result {
//        val appName = inputData.getString(KEY_APP_NAME) ?: return Result.failure(
//            workDataOf("error" to "Nombre de la aplicación no proporcionado.")
//        )
//        val appDescription = inputData.getString(KEY_APP_DESCRIPTION) ?: ""
//        val appVersion = inputData.getString(KEY_APP_VERSION) ?: ""
//        val iconUriString = inputData.getString(KEY_ICON_URI_STRING) ?: return Result.failure(
//            workDataOf("error" to "URI del icono no proporcionada.")
//        )
//        val apkUriString = inputData.getString(KEY_APK_URI_STRING) ?: return Result.failure(
//            workDataOf("error" to "URI del APK no proporcionada.")
//        )
//        val screenshotUrisStringArray = inputData.getStringArray(KEY_SCREENSHOT_URIS_STRING_ARRAY) ?: emptyArray()
//
//        // Paso 1: Notificación inicial al usuario
//        myAppsNotificationsUseCase.showInfo("Iniciando subida de '$appName'...")
//
//        return try {
//            myAppsNotificationsUseCase.showInfo("Procesando archivos de '$appName'...")
//            val iconUri = Uri.parse(iconUriString)
//            val apkUri = Uri.parse(apkUriString)
//            val screenshotUris = screenshotUrisStringArray.map { Uri.parse(it) }
//
//            val iconBytes = ImageUtils.processImageForUpload(applicationContext, iconUri)
//            val apkBytes = applicationContext.contentResolver.openInputStream(apkUri)?.readBytes()
//            val screenshotBytesList = screenshotUris.mapNotNull { ImageUtils.processImageForUpload(applicationContext, it) }
//
//            if (iconBytes == null || apkBytes == null || screenshotBytesList.isEmpty()) {
//                val errorMessage = "Error en el procesamiento de archivos para '$appName'."
//                myAppsNotificationsUseCase.showError(errorMessage, "Error de Procesamiento")
//                Log.e("UploadAppWorker", errorMessage)
//                return Result.failure(workDataOf("error" to errorMessage))
//            }
//
//            val newApp = App(
//                id = "", // El ID se generará en el backend
//                name = appName,
//                description = appDescription,
//                version = appVersion,
//                developerId = ,
//                releaseDate = TODO(),
//                rate = TODO(),
//                filesDetails = TODO(),
//                uiDetails = TODO(),
//            )
//
//            // Paso 3: Subir la aplicación (Llamada al Use Case)
//            myAppsNotificationsUseCase.showInfo("Subiendo '$appName' a los servidores...")
//            val result = myAppsUseCase.createApp(newApp, iconBytes, apkBytes, screenshotBytesList)
//
//            if (result.isSuccess) {
//                // Paso 4: Notificación de éxito
//                myAppsNotificationsUseCase.showAppAddedOrUpdatedSuccess(appName)
//                Log.d("UploadAppWorker", "Aplicación '$appName' añadida con éxito en segundo plano.")
//                Result.success()
//            } else {
//                // Paso 5: Notificación de error
//                val errorMessage = result.exceptionOrNull()?.message ?: "Error desconocido al añadir '$appName'."
//                myAppsNotificationsUseCase.showError(errorMessage, "Error al añadir aplicación")
//                Log.e("UploadAppWorker", "Error al añadir '$appName': $errorMessage")
//                Result.failure(workDataOf("error" to errorMessage))
//            }
//        } catch (e: Exception) {
//            // Manejo de excepciones generales
//            val errorMessage = e.message ?: "Excepción inesperada al añadir '$appName'."
//            myAppsNotificationsUseCase.showError(errorMessage, "Error Inesperado")
//            Log.e("UploadAppWorker", "Excepción al añadir '$appName':", e)
//            Result.failure(workDataOf("error" to errorMessage))
//        }
//    }
//}