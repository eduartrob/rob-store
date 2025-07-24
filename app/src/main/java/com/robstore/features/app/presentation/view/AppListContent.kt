package com.robstore.features.app.presentation.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robstore.features.app.domain.model.AppInfo


@Composable
fun AppListContent(
    appList: List<AppInfo>,
    onAppSelected: (AppInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .padding(start = 18.dp, end = 18.dp, top = 10.dp, bottom = 0.dp)
            .fillMaxSize(),
    ) {
        items(items = appList, key = { it.id }) { app ->
            AppCard(app = app, onClick = onAppSelected)
        }
    }
}