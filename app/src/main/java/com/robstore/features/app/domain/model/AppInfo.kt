package com.robstore.features.app.domain.model

import androidx.compose.ui.viewinterop.InteropView
import java.util.UUID

data class AppInfo(
    //val id: String = UUID.randomUUID().toString(),
    val name: String,
    val iconResId: Int, // Resource ID para el icono de la aplicación
    val description: String,
    val rate: Double,
    val size: String
)
