package com.robstore.features.app.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.robstore.features.app.domain.model.AppInfo
import com.robstore.features.app.presentation.state.AppFeatureScreen // Import the new state class
import com.robstore.features.home.presentation.viewModel.HomeViewModel


@Composable
fun AppListScreen(
    onBackToHomeDashboard: () -> Unit,
    onSubScreenChanged: (Boolean) -> Unit,
    homeViewModel: HomeViewModel = viewModel()
) {
    val appList by homeViewModel.appList.collectAsState()
    val appsLoading by homeViewModel.appsLoading.collectAsState()
    val appsError by homeViewModel.appsError.collectAsState()

    var currentAppFeatureScreen by remember { mutableStateOf<AppFeatureScreen>(AppFeatureScreen.AppListContent) }

    LaunchedEffect(currentAppFeatureScreen) {
        onSubScreenChanged(currentAppFeatureScreen is AppFeatureScreen.AppListContent)
    }

    LaunchedEffect(Unit) {
        homeViewModel.fetchApps()
    }

    when {
        appsLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        appsError != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Error al cargar apps: ${appsError}",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        else -> {
            when (currentAppFeatureScreen) {
                AppFeatureScreen.AppListContent -> {
                    AppListContent(
                        // Mapea la lista de dominio (App) a tu modelo de UI (AppInfo)
                        appList = appList.map { app ->
                            AppInfo(
                                id = app.id, // ID de la app
                                name = app.name,
                                description = app.description,
                                // Accede a los detalles anidados con el operador de seguridad '?'
                                iconUrl = app.filesDetails?.iconUrl ?: "https://placehold.co/50x50/cccccc/ffffff?text=Icon", // URL del icono, con placeholder
                                rate = (app.uiDetails?.rate ?: 0.0).toString(),
                                size = app.uiDetails?.size ?: "N/A" // Tamaño, con valor por defecto
                            )
                        },
                        onAppSelected = { appInfo ->
                            // Cuando se selecciona una AppInfo, busca el App original en la lista
                            // y navega al detalle con el modelo de dominio App
                            val selectedApp = appList.find { it.id == appInfo.id } // Usa el ID para una búsqueda más robusta
                            selectedApp?.let {
                                currentAppFeatureScreen = AppFeatureScreen.AppDetailScreen(it)
                            }
                        }
                    )
                }
                is AppFeatureScreen.AppDetailScreen -> {
                    // AppDetailScreen espera un objeto 'App' de dominio
                    AppDetailScreen(
                        app = (currentAppFeatureScreen as AppFeatureScreen.AppDetailScreen).app,
                        homeViewModel = homeViewModel, // <-- ¡Pasa la instancia del ViewModel existente!
                        onBack = { currentAppFeatureScreen = AppFeatureScreen.AppListContent }
                    )
                }
            }
        }
    }
}
