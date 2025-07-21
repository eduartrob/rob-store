package com.robstore.features.addApp.presentation.view

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

import android.R
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.CloudUpload // Icono para subir/cambiar imagen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField // Para los campos de texto
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.text.style.TextAlign


import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.robstore.features.addApp.presentation.viewModel.AddAppViewModel
import com.robstore.features.app.domain.model.AppInfo
import com.robstore.features.myApps.presentation.view.AppEditScreen


@Composable
fun AppAddScreen(
    onSave: (AppInfo) -> Unit,
    onCancel: () -> Unit,
    addAppViewModel: AddAppViewModel
) {
    val nameApp by addAppViewModel.nameInputText.collectAsState()
    val description by addAppViewModel.descriptionInputText.collectAsState()
    val version by addAppViewModel.versionInputText.collectAsState()
    val route by addAppViewModel.routeInputText.collectAsState()
    val size by addAppViewModel.sizeInputText.collectAsState()
    val photoApp by addAppViewModel.photoApp.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
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
            if (!photoApp.isNullOrBlank()) {
                AsyncImage(
                    model = photoApp,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Foto de perfil por defecto",
                    tint = Color.DarkGray,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                        .padding(20.dp)
                )
            }
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
            value = nameApp,
            onValueChange = {  },
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
            value = description,
            onValueChange = {  },
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
            value = version,
            onValueChange = {  },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Ruta APK",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = route,
            onValueChange = {  },
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
            value = size,
            onValueChange = {  },
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
                onClick = {},
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
//@Preview(showBackground = true)
//@Composable
//fun PreviewAppEditScreen() {
//    MaterialTheme {
//        val sampleApp = AppInfo(
//            name = "Nombre de App",
//            iconResId = R.drawable.ic_dialog_email, // Icono de ejemplo
//            description = "Descripción de la aplicación que se está editando.",
//            rate = 4.0,
//            size = "50 MB"
//        )
//        AppEditScreen(
//            app = sampleApp,
//            onSave = { updatedApp -> println("Guardado: $updatedApp") },
//            onCancel = { println("Edición cancelada") }
//        )
//    }
//}

