package com.robstore.features.app.presentation.state

import com.robstore.features.home.domain.model.App

sealed class AppFeatureScreen {
    object AppListContent : AppFeatureScreen()
    data class AppDetailScreen(val app: App) : AppFeatureScreen()

}
