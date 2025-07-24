package com.robstore.features.myApps.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow


import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.robstore.core.common.AppValidationState
import com.robstore.core.common.GeneralUiState
import com.robstore.features.myApps.domain.model.App
import com.robstore.features.myApps.presentation.viewModel.AddAppViewModel
import com.robstore.features.myApps.presentation.viewModel.MyAppsViewModel
import kotlinx.coroutines.delay


@Composable
fun AppAddScreen(
    onSave: (newApp: App, iconUri: Uri?, apkUri: Uri?, screenshotUris: List<Uri>) -> Unit,
    onCancel: () -> Unit,
    addAppViewModel: AddAppViewModel = viewModel(),
    myAppsViewModel: MyAppsViewModel,
) {
    // Observar los estados de los campos de texto desde el ViewModel
    val appName by addAppViewModel.nameInputText.collectAsState()
    val appDescription by addAppViewModel.descriptionInputText.collectAsState()
    val appVersion by addAppViewModel.versionInputText.collectAsState()

    // Observar las URIs de los archivos desde el ViewModel
    val selectedIcon by addAppViewModel.selectedIcon.collectAsState()
    val selectedApk by addAppViewModel.selectedApk.collectAsState()
    val selectedScreenshots by addAppViewModel.selectedScreenshots.collectAsState()

    // Observar los estados de validación desde el ViewModel
    val nameValidationState by addAppViewModel.nameValidationState.collectAsState()
    val descriptionValidationState by addAppViewModel.descriptionValidationState.collectAsState()
    val versionValidationState by addAppViewModel.versionValidationState.collectAsState()
    val iconValidationState by addAppViewModel.iconValidationState.collectAsState()
    val apkValidationState by addAppViewModel.apkValidationState.collectAsState()
    val screenshotsValidationState by addAppViewModel.screenshotsValidationState.collectAsState()

    val addUpdateAppUiState by myAppsViewModel.appUiState.collectAsStateWithLifecycle()


    // Launchers para seleccionar archivos
    val iconPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        addAppViewModel.onIconSelected(uri)
    }

    val apkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        addAppViewModel.onApkSelected(uri)
    }

    val screenshotPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        addAppViewModel.onScreenshotsSelected(uris)
    }

    LaunchedEffect(addUpdateAppUiState) {
        when (addUpdateAppUiState) {
            is GeneralUiState.Success -> {
                addAppViewModel.resetForm()
                addAppViewModel.resetUiState()
                onCancel()
            }
            is GeneralUiState.Error -> {
                delay(3000) // Muestra el error por 3 segundos
                addAppViewModel.resetUiState() // Resetea el estado de UI
            }
            else -> {}
        }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFf0f3f8))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Añadir Nueva Aplicación",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            if (selectedIcon != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = selectedIcon),
                    contentDescription = "Icono de la nueva aplicación",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Photo,
                    contentDescription = "Icono de aplicación por defecto",
                    tint = Color.DarkGray,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.LightGray)
                        .padding(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                enabled = addUpdateAppUiState !is GeneralUiState.Loading,
                onClick = { iconPickerLauncher.launch("image/*") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007aff))
            ) {
                Icon(imageVector = Icons.Filled.CloudUpload, contentDescription = "Subir imagen")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Seleccionar Imagen")
            }
            if (iconValidationState is AppValidationState.NotSelected) {
                Text(
                    text = "Selecciona un icono para la aplicación.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Text(
            text = "Nombre de la Aplicación",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            enabled = addUpdateAppUiState !is GeneralUiState.Loading,
            value = appName,
            onValueChange = { addAppViewModel.onNameChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center),
            isError = nameValidationState != null && nameValidationState !is AppValidationState.Valid
        )
        when (nameValidationState) {
            is AppValidationState.Empty -> Text("El nombre es obligatorio.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            is AppValidationState.TooShort -> Text("El nombre es muy corto (mín. 7).", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            is AppValidationState.TooLong -> Text("El nombre es muy largo (máx. 50).", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            else -> Unit
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Descripción",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            enabled = addUpdateAppUiState !is GeneralUiState.Loading,
            value = appDescription,
            onValueChange = { addAppViewModel.onDescriptionChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(bottom = 16.dp),
            minLines = 3,
            maxLines = 5,
            isError = descriptionValidationState != null && descriptionValidationState !is AppValidationState.Valid
        )
        when (descriptionValidationState) {
            is AppValidationState.Empty -> Text("La descripción es obligatoria.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            is AppValidationState.TooShort -> Text("La descripción es muy corta (mín. 10).", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            is AppValidationState.TooLong -> Text("La descripción es muy larga (máx. 500).", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            else -> Unit
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Versión",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            enabled = addUpdateAppUiState !is GeneralUiState.Loading,
            value = appVersion,
            onValueChange = { addAppViewModel.onVersionChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            isError = versionValidationState is AppValidationState.Empty
        )
        if (versionValidationState is AppValidationState.Empty) {
            Text("La versión es obligatoria.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Archivo APK",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = selectedApk?.lastPathSegment ?: "Ningún APK seleccionado",
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                enabled = addUpdateAppUiState !is GeneralUiState.Loading,
                onClick = { apkPickerLauncher.launch("application/vnd.android.package-archive") },
                border = BorderStroke(1.dp, Color(0xFF007aff)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF007aff)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Seleccionar APK")
            }
        }
        if (apkValidationState is AppValidationState.NotSelected) {
            Text(
                text = "Debes seleccionar un archivo APK.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Capturas de Pantalla ---
        Text(
            text = "Capturas de Pantalla",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth().height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(selectedScreenshots) { uri ->
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = "Captura de pantalla",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            }
            item {
                OutlinedButton(
                    enabled = addUpdateAppUiState !is GeneralUiState.Loading,
                    onClick = { screenshotPickerLauncher.launch("image/*") },
                    modifier = Modifier.size(100.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Icon(imageVector = Icons.Filled.AddCircleOutline, contentDescription = "Añadir captura")
                }
            }
        }
        if (screenshotsValidationState is AppValidationState.NotSelected) {
            Text(
                text = "Debes seleccionar al menos una captura de pantalla.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(50.dp).padding(horizontal = 4.dp),
                border = BorderStroke(1.dp, Color(0xFF007aff)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancelar", fontSize = 16.sp, color = Color(0xFF007aff))
            }
            Button(
                enabled = addUpdateAppUiState !is GeneralUiState.Loading,
                onClick = { addAppViewModel.validateAndSaveApp(onSave) },
                modifier = Modifier.weight(1f).height(50.dp).padding(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007aff),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (addUpdateAppUiState is GeneralUiState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Guardando...", fontSize = 16.sp)
                } else {
                    Text("Añadir", fontSize = 16.sp)
                }
            }
        }
        if (addUpdateAppUiState is GeneralUiState.Error && (addUpdateAppUiState as GeneralUiState.Error).message.isNotEmpty()) {
            Text(
                text = (addUpdateAppUiState as GeneralUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

