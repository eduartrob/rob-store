package com.robstore.core.hardware.camera.domain.repository

import android.net.Uri

interface CameraRepository{
    fun createImageUriForCamera(): Uri?
    suspend fun processCapturedPhoto(imageUri: Uri): Result<Uri>
}