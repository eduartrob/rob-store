package com.robstore.features.myApps.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.robstore.R
import com.robstore.features.app.domain.model.AppInfo


@Composable
fun MyAppsScreen(onBack: () -> Unit) {
    var selectedAppForDetail by remember { mutableStateOf<AppInfo?>(null) }
    var appToEdit by remember { mutableStateOf<AppInfo?>(null) }
    var appToDelete by remember { mutableStateOf<AppInfo?>(null) }



//    val myAppsList = remember {
//        listOf(
////            AppInfo(name = "WhatsApp", iconResId = R.drawable.logo, description = "Aplicación de mensajería instantánea y llamadas gratuitas.", rate = 4.7, size = "65 MB"),
////            AppInfo(name = "Spotify", iconResId = R.drawable.logo, description = "Transmisión de música, podcasts y audiolibros.", rate = 4.8, size = "105 MB"),
////            AppInfo(name = "Netflix", iconResId = R.drawable.logo, description = "Servicio de streaming de películas y series de televisión.", rate = 4.5, size = "75 MB"),
////            AppInfo(name = "Gmail", iconResId = R.drawable.logo, description = "Cliente de correo electrónico de Google.", rate = 4.3, size = "55 MB")
//        )
//    }.toMutableList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf0f3f8))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Aplicaciones",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.weight(0.15f))
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Filled.AddCircleOutline,
                    contentDescription = "Añadir aplicación",
                    modifier = Modifier.size(38.dp),
                    tint = Color(0xFF007aff)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
//            items(myAppsList, key = { it.name }) { app ->
//                AppCard(app = app, onClick = {
//                    selectedAppForDetail = it
//                })
//            }
        }
    }

    selectedAppForDetail?.let { app ->

        println("DEBUG: Intentando mostrar el Dialog para: ${app.name}")
        Dialog(
            onDismissRequest = { selectedAppForDetail = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                PopApp(
                    app = app,
                    onBack = { selectedAppForDetail = null },
                    onDelete = { appToDelete = it; selectedAppForDetail = null },
                    onEdit = { appToEdit = it; selectedAppForDetail = null }
                )
            }
        }
    }

    appToEdit?.let { app ->
        Dialog(
            onDismissRequest = { appToEdit = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth() // Mantén el ancho de la ventana de edición un poco más grande
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                // Aquí se carga tu AppEditScreen
//                AppEditScreen(
//                    app = app,
//                    onSave = { updatedApp ->
//                        val index = myAppsList.indexOfFirst { it.name == updatedApp.name }
//                        if (index != -1) {
//                            myAppsList[index] = updatedApp // Actualiza la app en la lista
//                        }
//                        appToEdit = null // Cierra el diálogo de edición
//                    },
//                    onCancel = { appToEdit = null } // Cierra el diálogo de edición sin guardar
//                )
            }
        }
    }



    // --- Diálogo para confirmar la eliminación (si se activa) ---
    appToDelete?.let { app ->
        AlertDialog(
            onDismissRequest = { appToDelete = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar '${app.name}'?") },
            confirmButton = {
                TextButton(onClick = {

                    appToDelete = null // Cierra el diálogo de confirmación
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { appToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }




}

@Preview(showBackground = true) // Añadido showSystemUi = true
@Composable
fun MyAppsScreenPreview() { // Renombrado a MyAppsScreenPreview para claridad
    MaterialTheme {
        MyAppsScreen(
            onBack = {} // Implementación vacía para que la previsualización funcione
        )
    }
}
