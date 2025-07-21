package com.robstore.features.app.presentation.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.robstore.features.app.domain.model.AppInfo


@Composable
fun AppListContent(
    appList: List<AppInfo>,
    onAppSelected: (AppInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        items(items = appList, key = { it.id }) { app -> // Usa app.id como la clave única
            AppCard(app = app, onClick = onAppSelected)
        }
    }
}