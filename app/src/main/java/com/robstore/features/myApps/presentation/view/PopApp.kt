package com.robstore.features.myApps.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star // Importa el icono de estrella
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Importa sp para el tamaño de la fuente
import coil.compose.rememberAsyncImagePainter
import com.robstore.features.app.domain.model.AppInfo
import com.robstore.features.myApps.domain.model.App


@Composable
fun PopApp(
    app: App, // Aceptar el modelo de dominio App
    onBack: () -> Unit,
    onDelete: (App) -> Unit, // Callback para eliminar la app (pasa App)
    onEdit: (App) -> Unit // Callback para editar la app (pasa App)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // <-- CAMBIO CLAVE: Usa fillMaxWidth() sin factor
            .background(Color(0xFFf0f3f8), shape = RoundedCornerShape(16.dp)) // Fondo con esquinas redondeadas
            .padding(16.dp), // Padding interno para el contenido del pop-up
        horizontalAlignment = Alignment.CenterHorizontally, // Centra horizontalmente
        verticalArrangement = Arrangement.Center // Centra verticalmente los elementos dentro de esta columna
    )  {
        // --- Botón de Cerrar (en la parte superior derecha, más estilo de "cerrar") ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End // Empuja el botón a la derecha
        ) {
            IconButton(onClick = onBack) { // Llama a onBack para cerrar la vista de detalles
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Cerrar",
                    tint = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // --- Información Principal de la Aplicación (Icono y Nombre) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = app.filesDetails?.iconUrl), // Carga la imagen desde la URL
                contentDescription = "${app.name} icon",
                modifier = Modifier
                    .size(70.dp) // Tamaño del icono ajustado para pop-up
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.LightGray), // Fondo para el icono
                contentScale = ContentScale.Crop // Escala de contenido
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = app.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Sección de Calificación y Tamaño ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.rate.toString(), // Accede a rate directamente del modelo App
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Calificación",
                    modifier = Modifier.size(18.dp),
                    tint = Color(0xFFFFD700)
                )
            }
            Text(
                text = "|",
                style = MaterialTheme.typography.titleMedium,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Text(
                text = app.uiDetails?.size ?: "N/A", // Accede a size de uiDetails
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Botón "Instalar" ---
        Button(
            onClick = { /* Acción para instalar/iniciar sesión */ println("Instalar/Iniciar Sesión clickeado para ${app.name}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .padding(vertical = 5.dp),
            shape = RoundedCornerShape(25.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF3F8B41),
                contentColor = Color.White
            )
        ) {
            Text(text = "Instalar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Descripción Completa de la Aplicación ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Acerca de esta app:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = app.description, // Accede a description directamente del modelo App
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray,
                    minLines = 3,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- Botones de Acción: Borrar y Editar ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { onDelete(app) }, // Pasa el objeto App completo
                modifier = Modifier.weight(1f).height(45.dp).padding(horizontal = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Filled.Delete, contentDescription = "Borrar", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Borrar", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onEdit(app) },
                modifier = Modifier.weight(1f).height(45.dp).padding(horizontal = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF007aff),
                    contentColor = Color.White
                )
            ) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Editar", modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Editar", fontWeight = FontWeight.Bold)
            }
        }
    }
}
