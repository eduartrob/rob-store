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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // Importa getValue
import androidx.compose.runtime.mutableStateOf // Importa mutableStateOf
import androidx.compose.runtime.remember // Importa remember
import androidx.compose.runtime.setValue // Importa setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun SearchAppScreen(onBack: () -> Unit) {
    // Estado para almacenar el texto que el usuario escribe en el campo de búsqueda
    var searchText by remember { mutableStateOf("") }

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

        // Campo de texto para la búsqueda
        OutlinedTextField(
            value = searchText,
            onValueChange = { newText ->
                searchText = newText
            },
            label = { Text("Nombre de la aplicación") }, // Etiqueta del campo de texto
            singleLine = true, // Permite una sola línea de texto
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp), // Padding horizontal para el TextField
        )

        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre el campo de búsqueda y los resultados

        // Contenido de la pantalla de búsqueda (aquí irían los resultados)
        if (searchText.isEmpty()) {
            Text(
                text = "Escribe el nombre de una aplicación para buscar.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray
            )
        } else {
            // Aquí iría la lógica para mostrar los resultados de la búsqueda
            // Por ejemplo, una LazyColumn con AppCard para cada resultado
            Text(
                text = "Mostrando resultados para: \"$searchText\"",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray
            )
            // LazyColumn para los resultados de búsqueda:
            // LazyColumn {
            //     items(searchResults) { appInfo ->
            //         AppCard(app = appInfo, onClick = { /* Navegar a detalles */ })
            //     }
            // }
        }
    }
}
