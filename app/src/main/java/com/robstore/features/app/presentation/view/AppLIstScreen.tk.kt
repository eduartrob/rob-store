package com.robstore.features.app.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robstore.R
import com.robstore.features.app.domain.model.AppInfo
import com.robstore.features.app.presentation.state.AppFeatureScreen // Import the new state class
import com.robstore.features.app.presentation.view.AppListContent // The actual list Composable



@Composable
fun AppListScreen(
    onBackToHomeDashboard: () -> Unit,
    onSubScreenChanged: (Boolean) -> Unit
) {
    val appList = remember {
        listOf(
            AppInfo("Facebook", iconResId =  R.drawable.logoprueba,  "Conecta con amigos y familiares.", rate = 4.3, size = "23 MB"),
            AppInfo("WhatsApp", iconResId =  R.drawable.logoprueba,  "Mensajería instantánea y llamadas.", rate = 4.3, size = "23 MB"),
            AppInfo("Instagram", iconResId =  R.drawable.logoprueba,  "Comparte fotos y videos con tus seguidores.", rate = 4.3, size = "23 MB"),
            AppInfo("X (Twitter)", iconResId =  R.drawable.logoprueba,  "Deportes, entretenimiento y conversación.", rate = 4.3, size = "23 MB"),
            AppInfo("Spotify", iconResId =  R.drawable.logoprueba, "Millones de canciones y podcasts gratis.", rate = 4.3, size = "23 MB"),
            AppInfo("Netflix", iconResId =  R.drawable.logoprueba, "Series de TV y películas en streaming.", rate = 4.3, size = "23 MB"),
            AppInfo("YouTube", iconResId =  R.drawable.logoprueba, "Mira tus videos favoritos, canales y sube tu propio contenido.", rate = 4.3, size = "23 MB"),
            AppInfo("TikTok", iconResId =  R.drawable.logoprueba, "Videos cortos para ti.", rate = 4.3, size = "23 MB"),
            AppInfo("Google Maps", iconResId =  R.drawable.logoprueba, "Navega por el mundo de forma más rápida y sencilla.", rate = 4.3, size = "23 MB"),
            AppInfo("Gmail", iconResId =  R.drawable.logoprueba, "Email de Google, gratis y seguro.", rate = 4.3, size = "23 MB")
        )
    }

    var currentAppFeatureScreen by remember { mutableStateOf<AppFeatureScreen>(AppFeatureScreen.AppListContent) }



    LaunchedEffect(currentAppFeatureScreen) {
        onSubScreenChanged(currentAppFeatureScreen is AppFeatureScreen.AppListContent)
    }

    when (currentAppFeatureScreen) {
        AppFeatureScreen.AppListContent -> {
            AppListContent(
                appList = appList,
                onAppSelected = { app -> currentAppFeatureScreen = AppFeatureScreen.AppDetailScreen(app) }
            )
        }
        is AppFeatureScreen.AppDetailScreen -> {
            AppDetailScreen(
                app = (currentAppFeatureScreen as AppFeatureScreen.AppDetailScreen).app,
                onBack = { currentAppFeatureScreen = AppFeatureScreen.AppListContent }
            )
        }

    }
}
