package com.robstore.features.myApps.presentation.view

import android.R
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview // Importa @Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp // Importa sp para el tamaño de la fuente
import com.robstore.features.app.domain.model.AppInfo


@Composable
fun PopApp(
    app: AppInfo,
    onBack: () -> Unit, // Callback para regresar (actúa como "cerrar el pop-up")
    onDelete: (AppInfo) -> Unit, // Callback para eliminar la app
    onEdit: (AppInfo) -> Unit // Callback para editar la app
) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // Ocupa el 90% del ancho del diálogo
            .wrapContentHeight() // Ajusta la altura al contenido
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
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Puedes cambiar a Icons.Default.Close si tienes ese icono
                    contentDescription = "Cerrar",
                    tint = Color.Gray // Un color sutil para el botón de cerrar
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // --- Información Principal de la Aplicación (Icono y Nombre) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            Image(
//                painter = painterResource(id = app.iconResId),
//                contentDescription = "${app.name} icon",
//                modifier = Modifier
//                    .size(70.dp) // Tamaño del icono ajustado para pop-up
//                    .clip(RoundedCornerShape(16.dp))
//                    .background(Color.LightGray)
//            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = app.name,
                style = MaterialTheme.typography.headlineMedium, // Tamaño del texto ajustado
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
                    text = app.rate.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Calificación",
                    modifier = Modifier.size(18.dp), // Tamaño del icono ajustado
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
                text = app.size,
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
                .height(55.dp) // Altura ajustada
                .padding(vertical = 5.dp),
            shape = RoundedCornerShape(25.dp), // Esquinas más redondeadas
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
                    text = app.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray
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
                onClick = { onDelete(app) },
                modifier = Modifier.weight(1f).height(45.dp).padding(horizontal = 4.dp), // Altura y padding ajustados
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
                modifier = Modifier.weight(1f).height(45.dp).padding(horizontal = 4.dp), // Altura y padding ajustados
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

// --- Función de Previsualización para PopApp ---
//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun PreviewPopApp() {
//    MaterialTheme {
//        val sampleApp = AppInfo(
//            name = "App Flotante",
//            iconResId = R.drawable.ic_dialog_info, // Usando un icono de Android genérico para la preview
//            description = "Esta es una descripción más corta para una ventana flotante, para que el contenido sea más compacto. Permite visualizar el pop-up de forma rápida.",
//            rate = 4.5,
//            size = "45 MB"
//        )
//
//        PopApp(
//            app = sampleApp,
//            onBack = { println("PopApp cerrada en Preview") },
//            onDelete = { app -> println("Borrar ${app.name} clickeado en PopApp Preview") },
//            onEdit = { app -> println("Editar ${app.name} clickeado en PopApp Preview") }
//        )
//    }
//}
