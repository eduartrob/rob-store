package com.robstore.features.myApps.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robstore.features.app.domain.model.AppInfo
import com.robstore.features.myApps.domain.model.App
import com.robstore.features.myApps.presentation.viewModel.MyAppsViewModel

import com.robstore.features.addApp.presentation.view.AppAddScreen



@Composable
fun MyAppsScreen(
    myAppsViewModel: MyAppsViewModel,
    onBack: () -> Unit
) {
    val myAppsList by myAppsViewModel.myAppsList.collectAsState()
    val myAppsLoading by myAppsViewModel.myAppsLoading.collectAsState()
    val myAppsError by myAppsViewModel.myAppsError.collectAsState()

    var selectedAppForDetail by remember { mutableStateOf<App?>(null) } // Usar App de dominio
    var appToEdit by remember { mutableStateOf<App?>(null) } // Usar App de dominio
    var appToDelete by remember { mutableStateOf<App?>(null) } // Usar App de dominio
    var showAddAppScreen by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        myAppsViewModel.fetchMyApps()
    }

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
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Text(
                text = "Mis Aplicaciones",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.weight(0.15f))
            IconButton(onClick = { showAddAppScreen = true }) {
                Icon(
                    imageVector = Icons.Filled.AddCircleOutline,
                    contentDescription = "Añadir aplicación",
                    modifier = Modifier.size(38.dp),
                    tint = Color(0xFF007aff)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        when {
            myAppsLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            myAppsError != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "No hay nada por aqui",
                        color = Color(0xFFc1c7c6),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .padding(16.dp)
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(items = myAppsList, key = { it.id }) { app ->
                        AppCard(
                            app = AppInfo(
                                name = app.name,
                                description = app.description,
                                iconUrl = app.filesDetails?.iconUrl
                                    ?: "https://placehold.co/50x50/cccccc/ffffff?text=Icon",
                                rate = app.rate.toString(),
                                size = app.uiDetails?.size ?: "N/A",
                                id = null.toString()
                            ),
                            onClick = { selectedAppForDetail = app }
                        )
                    }
                }
            }
        }
    }

    selectedAppForDetail?.let { app ->
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
                // Reutilizamos PopApp, pasándole el App de dominio
                PopApp(
                    app = app,
                    onBack = { selectedAppForDetail = null },
                    onDelete = { appToDelete = it; selectedAppForDetail = null }, // Pasa el App completo
                    onEdit = { appToEdit = it; selectedAppForDetail = null }, // Pasa el App completo
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
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                // Aquí se carga tu AppEditScreen
                AppEditScreen(
                    app = app, // Pasa el objeto App de dominio
                    onSave = { updatedApp, newIconUri, newApkUri, newScreenshotUris ->
                        // Llama a la función updateApp del ViewModel
                        myAppsViewModel.updateApp(updatedApp, newIconUri, newApkUri, newScreenshotUris)
                        appToEdit = null // Cierra el diálogo de edición después de guardar
                    },
                    onCancel = { appToEdit = null } // Cierra el diálogo de edición sin guardar
                )
            }
        }
    }

    appToDelete?.let { app ->
        AlertDialog(
            onDismissRequest = { appToDelete = null },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar '${app.name}'?") },
            confirmButton = {
                TextButton(onClick = {
                    app.id?.let { myAppsViewModel.deleteApp(it) } // Llama a la función de eliminación en MyAppsViewModel
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

    if (showAddAppScreen) {
        Dialog(
            onDismissRequest = { showAddAppScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                AppAddScreen(
                    onSave = { newApp, iconUri, apkUri, screenshotUris ->
                        myAppsViewModel.addApp(newApp, iconUri, apkUri, screenshotUris)
                    },
                    onCancel = { showAddAppScreen = false },
                    addAppViewModel = viewModel(),
                    myAppsViewModel = myAppsViewModel
                )
            }
        }
    }
}
