package com.robstore.features.authentication.recoveryPassword.presentation.view

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SuccessMessageOverlay(message: String) {
    var scale by remember { mutableStateOf(0f) }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = tween(durationMillis = 500, delayMillis = 100) // Animación suave
    )

    // Lanzar el efecto para iniciar la animación cuando el Composable se muestra
    LaunchedEffect(Unit) {
        scale = 1f // Inicia la animación de escala
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)), // Fondo oscuro semitransparente
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth(0.8f) // Ancho del contenido
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            // Icono de palomita verde animado
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Éxito",
                tint = Color(0xFF4CAF50), // Un verde vibrante
                modifier = Modifier
                    .size(80.dp) // Tamaño grande para el icono
                    .scale(animatedScale) // Aplica la animación de escala
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡Contraseña Actualizada!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message, // El mensaje que viene del ViewModel
                fontSize = 18.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}
