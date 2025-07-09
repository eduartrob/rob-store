package com.robstore.features.Loading

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // Importa Color

@Composable
fun LoadingOverlay(
    isLoading: Boolean,
    content: @Composable () -> Unit // Este es el contenido de tu pantalla
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Dibuja el contenido de la pantalla *debajo*
        content()

        // 2. SI isLoading es true, dibuja el overlay *encima*
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)), // Fondo semi-transparente
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White) // Spinner blanco
            }
        }
    }
}
