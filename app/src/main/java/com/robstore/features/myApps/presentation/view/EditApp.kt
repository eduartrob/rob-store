package com.robstore.features.myApps.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.CloudUpload // Icono para subir/cambiar imagen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api // Para OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField // Para los campos de texto
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.draw.clip

import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.style.TextAlign


import androidx.compose.ui.unit.sp
import com.robstore.features.app.domain.model.AppInfo // Asegúrate de la importación correcta de AppInfo


// Composable para la Pantalla de Edición de la Aplicación
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AppEditScreen(
    app: AppInfo,
    onSave: (AppInfo) -> Unit, // Callback para guardar los cambios
    onCancel: () -> Unit // Callback para cancelar la edición
) {
    // Estados mutables para cada campo editable
    var appName by remember { mutableStateOf(app.name) }
    var appDescription by remember { mutableStateOf(app.description) }
    var appVersion by remember { mutableStateOf("1.0.0") } // Valor por defecto, o app.version si lo añades a AppInfo
    var appApkPath by remember { mutableStateOf("/sdcard/app.apk") } // Valor por defecto
    var appSize by remember { mutableStateOf(app.size) }

    Column(
        modifier = Modifier
            .fillMaxWidth() // Ocupa todo el ancho
            .wrapContentHeight() // Ajusta la altura al contenido
            .background(Color(0xFFf0f3f8)) // Fondo ligero
            .padding(16.dp) // Padding general de la pantalla
            .verticalScroll(rememberScrollState()), // Habilita el desplazamiento vertical
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Editar Aplicación",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- Sección de Cambiar Imagen ---
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            Image(
                painter = painterResource(id = app.iconResId),
                contentDescription = "Icono de la aplicación actual",
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { /* Acción para abrir selector de imagen */ println("Cambiar imagen clickeado") },
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
            textAlign = TextAlign.Center // Centra el texto del label
        )
        OutlinedTextField(
            value = appName,
            onValueChange = { appName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center) // Centra el texto de entrada
        )

        // --- Descripción ---
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center // Centra el texto del label
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
            textAlign = TextAlign.Center // Centra el texto del label
        )
        OutlinedTextField(
            value = appVersion,
            onValueChange = { appVersion = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // --- Actualizar APK (Simulado como campo de texto para ruta) ---
        Text(
            text = "Ruta APK",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = appApkPath,
            onValueChange = { appApkPath = it },
            // label = { Text("Ruta APK") }, // Eliminado
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

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
            // label = { Text("Tamaño") }, // Eliminado
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
                        size = appSize
                    )
                    onSave(updatedApp)
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


// --- Función de Previsualización ---
@Preview(showBackground = true)
@Composable
fun PreviewAppEditScreen() {
    MaterialTheme {
        val sampleApp = AppInfo(
            name = "Nombre de App",
            iconResId = R.drawable.ic_dialog_email, // Icono de ejemplo
            description = "Descripción de la aplicación que se está editando.",
            rate = 4.0,
            size = "50 MB"
        )
        AppEditScreen(
            app = sampleApp,
            onSave = { updatedApp -> println("Guardado: $updatedApp") },
            onCancel = { println("Edición cancelada") }
        )
    }
}

