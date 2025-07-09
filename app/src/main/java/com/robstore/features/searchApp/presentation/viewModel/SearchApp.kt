package com.robstore.features.searchApp.presentation.viewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SearchAppScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf0f3f8)) // Fondo ligero para la pantalla de búsqueda
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Barra de Título con botón de regreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Text(
                text = "Buscar Aplicaciones",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),

            )
            Spacer(modifier = Modifier.weight(0.15f))
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Contenido de la pantalla de búsqueda
        Text(
            text = "Aquí iría la interfaz de búsqueda de aplicaciones.",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.DarkGray
        )
        // Puedes añadir un TextField, un botón de búsqueda, una LazyColumn para resultados, etc. aquí
        // Ejemplo de un campo de texto de búsqueda:
        // OutlinedTextField(
        //     value = searchText,
        //     onValueChange = { searchText = it },
        //     label = { Text("Escribe para buscar...") },
        //     modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        // )
    }
}