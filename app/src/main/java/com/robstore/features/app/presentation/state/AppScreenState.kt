package com.robstore.features.app.presentation.state

import com.robstore.features.app.domain.model.AppInfo

sealed class AppFeatureScreen {
    object AppListContent : AppFeatureScreen()
    data class AppDetailScreen(val app: AppInfo) : AppFeatureScreen()

}
