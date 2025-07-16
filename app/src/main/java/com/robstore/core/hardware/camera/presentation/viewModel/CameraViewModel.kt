package com.robstore.core.hardware.camera.presentation.viewModel

import android.content.Context
import android.net.Uri
import com.robstore.core.hardware.camera.domain.repository.CameraRepository
import com.robstore.core.store.local.dataStore.DataStoreManager
import com.robstore.core.store.local.dataStore.PreferenceKeys

class CameraViewModel(
    private val cameraRepository: CameraRepository,
    private val dataStoreManager: DataStoreManager
) {

    private var tempCameraUri: Uri? = null

    fun prepareCameraCapture(): Uri? {
        tempCameraUri = cameraRepository.createImageUriForCamera()
        return tempCameraUri
    }

    suspend fun processCapturedPhoto(success: Boolean, context: Context): Uri? {
        if (!success) {
            tempCameraUri = null
            return null
        }

        tempCameraUri?.let { uri ->
            val result = cameraRepository.processCapturedPhoto(uri)
            if (result.isSuccess) {
                val photoUri = result.getOrNull()
                photoUri?.let {
                    // Opcional: sube la foto o guarda en DataStore aquí mismo
                    // O simplemente retorna la Uri
                }
                return photoUri
            }
        }
        return null
    }

    suspend fun clearCapturedImage() {
        dataStoreManager.deleteKey(PreferenceKeys.USER_PROFILE_PICTURE_URI)
    }
}
