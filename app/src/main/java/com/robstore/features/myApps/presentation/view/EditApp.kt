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
import androidx.compose.material.icons.filled.CloudUpload // Icono para subir/cambiar imagen
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api // Para OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField // Para los campos de texto
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
import coil.compose.rememberAsyncImagePainter
import com.robstore.features.myApps.domain.model.AppFilesDetails
import com.robstore.features.myApps.domain.model.AppUIDetails


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppEditScreen(
    app: App, // Cambiado de AppInfo a App
    onSave: (updatedApp: App, newIconUri: Uri?, newApkUri: Uri?, newScreenshotUris: List<Uri>) -> Unit,
    onCancel: () -> Unit // Callback para cancelar la edición
) {
    // Estados mutables para cada campo editable
    var appName by remember { mutableStateOf(app.name) }
    var appDescription by remember { mutableStateOf(app.description) }
    var appVersion by remember { mutableStateOf(app.version) } // Desde el modelo App
    var appSize by remember { mutableStateOf(app.uiDetails?.size ?: "N/A") } // Desde el modelo App

    // Estados para las URIs de los nuevos archivos seleccionados
    var selectedIconUri by remember { mutableStateOf<Uri?>(null) }
    var selectedApkUri by remember { mutableStateOf<Uri?>(null) }
    val selectedScreenshotUris = remember { mutableStateListOf<Uri>().apply {
        app.filesDetails?.screenshots?.map { it.toUri() }?.let { addAll(it) }
    }}

    // Launchers para seleccionar archivos
    val iconPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedIconUri = it }
    }

    val apkPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent() // O ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { selectedApkUri = it }
    }

    val screenshotPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        selectedScreenshotUris.addAll(uris)
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
            val currentIconModel = selectedIconUri ?: app.filesDetails?.iconUrl
            if (currentIconModel != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = currentIconModel),
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
                onClick = { iconPickerLauncher.launch("image/*") }, // Abre el selector de imágenes
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007aff))
            ) {
                Icon(imageVector = Icons.Filled.CloudUpload, contentDescription = "Subir imagen")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cambiar Imagen")
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
            value = appName,
            onValueChange = { appName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
        )

        // --- Descripción ---
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = appDescription,
            onValueChange = { appDescription = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(bottom = 16.dp),
            minLines = 3,
            maxLines = 5
        )

        // --- Versión ---
        Text(
            text = "Versión",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = appVersion,
            onValueChange = { appVersion = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

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
                text = selectedApkUri?.lastPathSegment ?: app.filesDetails?.appFileUrl?.substringAfterLast('/') ?: "Ningún APK seleccionado",
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedButton(
                onClick = { apkPickerLauncher.launch("application/vnd.android.package-archive") }, // Filtra por APKs
                border = BorderStroke(1.dp, Color(0xFF007aff)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF007aff)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Seleccionar APK")
            }
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
            items(selectedScreenshotUris) { uri ->
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
                    onClick = { screenshotPickerLauncher.launch("image/*") }, // Permite seleccionar múltiples imágenes
                    modifier = Modifier.size(100.dp),
                    border = BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) {
                    Icon(imageVector = Icons.Filled.AddCircleOutline, contentDescription = "Añadir captura")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Tamaño ---
        Text(
            text = "Tamaño",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = appSize,
            onValueChange = { appSize = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // --- Botones de Cancelar y Guardar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround // Espacia los botones
        ) {
            OutlinedButton( // Cambiado a OutlinedButton
                onClick = onCancel,
                modifier = Modifier.weight(1f).height(50.dp).padding(horizontal = 4.dp),
                border = BorderStroke(1.dp, Color(0xFF007aff)), // Borde azul
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White), // Texto blanco
                shape = RoundedCornerShape(8.dp) // Menos redondeado
            ) {
                Text("Cancelar", fontSize = 16.sp, color = Color(0xFF007aff)) // Color del texto del botón cancelado
            }
            Button(
                onClick = {
                    val updatedApp = app.copy(
                        name = appName,
                        description = appDescription,
                        version = appVersion,
                        uiDetails = app.uiDetails?.copy(size = appSize) ?: AppUIDetails(size = appSize)
                    )
                    onSave(updatedApp, selectedIconUri, selectedApkUri, selectedScreenshotUris)
                },
                modifier = Modifier.weight(1f).height(50.dp).padding(horizontal = 4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007aff), // Color azul para el fondo
                    contentColor = Color.White // Color blanco para el texto
                ),
                shape = RoundedCornerShape(8.dp) // Menos redondeado para ser consistente
            ) {
                Text("Guardar", fontSize = 16.sp)
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAppEditScreen() {
    MaterialTheme {
        val sampleApp = App(
            id = "sample_id_123",
            name = "Mi App Increíble",
            description = "Esta es una descripción detallada de mi aplicación. Permite a los usuarios hacer cosas increíbles y es muy fácil de usar.",
            version = "1.2.3",
            developerId = "dev_123",
            releaseDate = "2024-07-21",
            rate = 4.7,
            filesDetails = AppFilesDetails(
                iconUrl = "https://placehold.co/100x100/A020F0/ffffff?text=Icon", // Púrpura
                appFileUrl = "https://example.com/app.apk",
                screenshots = listOf(
                    "https://placehold.co/100x100/FF5733/ffffff?text=SS1", // Naranja
                    "https://placehold.co/100x100/33FF57/ffffff?text=SS2"  // Verde
                )
            ),
            uiDetails = AppUIDetails(size = "75 MB")
        )
        AppEditScreen(
            app = sampleApp,
            onSave = { updatedApp, newIconUri, newApkUri, newScreenshotUris ->
                println("Guardado: $updatedApp")
                println("Nuevo Icono URI: $newIconUri")
                println("Nuevo APK URI: $newApkUri")
                println("Nuevas Capturas URI: $newScreenshotUris")
            },
            onCancel = { println("Edición cancelada") }
        )
    }
}
