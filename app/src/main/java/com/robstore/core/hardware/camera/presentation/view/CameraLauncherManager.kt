package com.robstore.core.hardware.camera.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.robstore.core.hardware.camera.presentation.viewModel.CameraViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

data class CameraLauncherController(
    val launchCamera: () -> Unit
)

@Composable
fun CameraLauncherManager(
    cameraViewModel: CameraViewModel,
    coroutineScope: CoroutineScope,
    onPhotoCaptured: (Uri) -> Unit
): CameraLauncherController{
    val context = LocalContext.current
    val cameraPermission = Manifest.permission.CAMERA

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        coroutineScope.launch {
            val processedUri = cameraViewModel.processCapturedPhoto(success, context)
            if (processedUri != null) {
                onPhotoCaptured(processedUri)
            }
        }
    }

    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = cameraViewModel.prepareCameraCapture()
            uri?.let {
                takePictureLauncher.launch(it)
            }
        } else {
            Toast.makeText(context, "Permiso denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCamera() {
        if (ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_GRANTED) {
            val uri = cameraViewModel.prepareCameraCapture()
            uri?.let { takePictureLauncher.launch(it) }
        } else {
            requestCameraPermissionLauncher.launch(cameraPermission)
        }
    }

    return remember {
        CameraLauncherController(launchCamera = ::launchCamera)
    }
}

