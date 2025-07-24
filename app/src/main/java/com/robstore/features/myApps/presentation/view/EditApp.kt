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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload // Icono para subir/cambiar imagen
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api // Para OutlinedTextField
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField // Para los campos de texto
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow


import androidx.compose.ui.unit.sp
import com.robstore.features.myApps.domain.model.App
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.robstore.core.common.AppValidationState
import com.robstore.core.common.GeneralUiState
import com.robstore.features.myApps.domain.model.AppFilesDetails
import com.robstore.features.myApps.domain.model.AppUIDetails
import com.robstore.features.myApps.presentation.viewModel.EditAppViewModel
import com.robstore.features.myApps.presentation.viewModel.MyAppsViewModel
import kotlinx.coroutines.delay


@Composable
fun AppEditScreen(
    app: App,
    onSave: (updatedApp: App, newIconUri: Uri?, newApkUri: Uri?, newScreenshotUris: List<Uri>) -> Unit,
    onCancel: () -> Unit,
    editAppViewModel: EditAppViewModel = viewModel(),
    myAppsViewModel: MyAppsViewModel
) {
    // Observar los estados de los campos de texto desde el ViewModel
    val appName by editAppViewModel.nameInputText.collectAsState()
    val appDescription by editAppViewModel.descriptionInputText.collectAsState()
    val appVersion by editAppViewModel.versionInputText.collectAsState()

    // Observar las URIs de los archivos desde el ViewModel
    val selectedIcon by editAppViewModel.selectedIcon.collectAsState()
    val selectedApk by editAppViewModel.selectedApk.collectAsState()
    val selectedScreenshots by editAppViewModel.selectedScreenshots.collectAsState()

    // Observar los estados de validación desde el ViewModel
    val nameValidationState by editAppViewModel.nameValidationState.collectAsState()
    val descriptionValidationState by editAppViewModel.descriptionValidationState.collectAsState()
    val versionValidationState by editAppViewModel.versionValidationState.collectAsState()
    val iconValidationState by editAppViewModel.iconValidationState.collectAsState()
    val apkValidationState by editAppViewModel.apkValidationState.collectAsState()
    val screenshotsValidationState by editAppViewModel.screenshotsValidationState.collectAsState()

//    val editAppUiState by editAppViewModel.editAppUiState.collectAsState()
    val addUpdateAppUiState by myAppsViewModel.appUiState.collectAsStateWithLifecycle()

    // Launchers para seleccionar archivos
    val iconPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        editAppViewModel.onIconSelected(uri)
    }

    val apkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        editAppViewModel.onApkSelected(uri)
    }

    val screenshotPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        editAppViewModel.onScreenshotsSelected(uris)
    }

    LaunchedEffect(app) {
        editAppViewModel.initializeFromApp(app)
    }

    LaunchedEffect(addUpdateAppUiState) {
        Log.e("EditApp", "El estado ha cambiado")
        when (addUpdateAppUiState) {
            is GeneralUiState.Success -> {
                Log.e("EditApp", "El estado ha cambiado a true")
                editAppViewModel.resetForm()
                editAppViewModel.resetUiState()
                onCancel()
            }
            is GeneralUiState.Error -> {
                delay(3000)
                editAppViewModel.resetUiState()
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
            text = "Editar Aplicación",
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
                    contentDescription = "Icono de la aplicación actual",
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
                onClick = { iconPickerLauncher.launch("image/*") }, // Abre el selector de imágenes
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007aff))
            ) {
                Icon(imageVector = Icons.Filled.CloudUpload, contentDescription = "Subir imagen")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cambiar Imagen")
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

        // --- Nombre de la Aplicación ---
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
            onValueChange = { editAppViewModel.onNameChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
        )
        when (nameValidationState) {
            is AppValidationState.Empty -> Text("El nombre es obligatorio.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            is AppValidationState.TooShort -> Text("El nombre es muy corto (mín. 7).", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            is AppValidationState.TooLong -> Text("El nombre es muy largo (máx. 50).", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            else -> Unit
        }
        Spacer(modifier = Modifier.height(12.dp))

        // --- Descripción ---
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            enabled = addUpdateAppUiState !is GeneralUiState.Loading,
            value = appDescription,
            onValueChange = { editAppViewModel.onDescriptionChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            minLines = 3,
            maxLines = 20
        )
        when (descriptionValidationState) {
            is AppValidationState.Empty -> Text("La descripción es obligatoria.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            is AppValidationState.TooShort -> Text("La descripción es muy corta (mín. 10).", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            is AppValidationState.TooLong -> Text("La descripción es muy larga (máx. 4000).", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            else -> Unit
        }
        Spacer(modifier = Modifier.height(12.dp))

        // --- Versión ---
        Text(
            text = "Versión",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            enabled = addUpdateAppUiState !is GeneralUiState.Loading,
            value = appVersion,
            onValueChange = { editAppViewModel.onVersionChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        if (versionValidationState is AppValidationState.Empty) {
            Text("La versión es obligatoria.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(12.dp))

        // --- APK ---
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
                onClick = { apkPickerLauncher.launch("application/vnd.android.package-archive") }, // Filtra por APKs
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
                Box( // Usamos Box para superponer el botón de borrar
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = "Captura de pantalla",
                        modifier = Modifier.matchParentSize(), // Ocupa todo el Box
                        contentScale = ContentScale.Crop
                    )
                    // Botón de borrar (la "X")
                    IconButton(
                        onClick = { editAppViewModel.removeScreenshot(uri) },
                        modifier = Modifier
                            .align(Alignment.TopEnd) // Alinea en la esquina superior derecha
                            .padding(4.dp) // Pequeño padding desde el borde
                            .size(24.dp) // Tamaño del botón
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50)) // Fondo semitransparente circular
                            .clip(RoundedCornerShape(50)), // Para que el fondo sea perfectamente circular
                        enabled = addUpdateAppUiState !is GeneralUiState.Loading // Deshabilitar si está cargando
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close, // Icono de 'X'
                            contentDescription = "Eliminar captura",
                            tint = Color.White, // Color del icono
                            modifier = Modifier.size(16.dp) // Tamaño del icono dentro del botón
                        )
                    }
                }
            }
            item {
                // Solo mostrar el botón de añadir si no se ha alcanzado el límite máximo de capturas
                if (selectedScreenshots.size < 5) {
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
        }
        when (screenshotsValidationState) {
            AppValidationState.NotSelected -> Text("Debes seleccionar al menos una captura de pantalla.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            AppValidationState.TooMany -> Text("Solo puedes subir un máximo de 5 capturas de pantalla.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            else -> Unit
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Botones de Cancelar y Guardar ---
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
                onClick = { editAppViewModel.validateAndUpdateApp(onSave) },
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
                    Text("Guardar", fontSize = 16.sp)
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
