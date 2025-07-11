package com.robstore.core.hardware.camera.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.robstore.core.hardware.camera.domain.repository.CameraRepository
import java.io.File
import java.io.IOException

class CameraManager(
    private val context: Context
): CameraRepository {
    override fun createImageUriForCamera(): Uri? {
        return try {
            // Crea un directorio 'images' dentro del directorio de caché de tu aplicación
            val photoDir = File(context.cacheDir, "images").apply {
                mkdirs() // Asegura que el directorio exista
            }
            // Crea un archivo temporal para la foto
            val newFile = File(photoDir, "temp_photo_${System.currentTimeMillis()}.jpg")

            // Obtiene la URI segura usando FileProvider
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider", // Debe coincidir con el 'authorities' en el Manifest
                newFile
            )
        } catch (e: IOException) {
            Log.e("CameraManager", "Error al crear archivo temporal para la cámara: ${e.message}", e)
            null
        } catch (e: IllegalArgumentException) {
            Log.e("CameraManager", "Error de configuración de FileProvider: ${e.message}", e)
            null
        }

    }

    override suspend fun processCapturedPhoto(imageUri: Uri): Result<Uri> {
        return try {
            Result.success(imageUri)
        } catch (e: Exception) {
            Log.e("CameraManager", "Error al procesar la foto capturada: ${e.message}", e)
            Result.failure(e)
        }
    }

}