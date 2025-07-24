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
import androidx.compose.ui.unit.sp
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
                    text = "Error al cargar apps: $appsError",
                    color = Color(0xFFc1c7c6),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        else -> {
            when (currentAppFeatureScreen) {
                AppFeatureScreen.AppListContent -> {
                    AppListContent(
                        appList = appList.map { app ->
                            AppInfo(
                                id = app.id,
                                name = app.name,
                                description = app.description,
                                iconUrl = app.filesDetails?.iconUrl ?: "https://placehold.co/50x50/cccccc/ffffff?text=Icon",
                                rate = (app.uiDetails?.rate ?: 0.0).toString(),
                                size = app.uiDetails?.size ?: "N/A"
                            )
                        },
                        onAppSelected = { appInfo ->
                            val selectedApp = appList.find { it.id == appInfo.id }
                            selectedApp?.let {
                                currentAppFeatureScreen = AppFeatureScreen.AppDetailScreen(it)
                            }
                        }
                    )
                }
                is AppFeatureScreen.AppDetailScreen -> {
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
