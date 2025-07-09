package com.robstore.features.myApps.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robstore.features.app.domain.model.AppInfo

@Composable
fun AppCard(app: AppInfo, onClick: (AppInfo) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp)
            .clickable { onClick(app) }, // Hacemos la card clickeable
        shape = RoundedCornerShape(12.dp), // Esquinas redondeadas para la card
        colors = CardDefaults.cardColors(containerColor = Color(0xFFf0f3f8))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp), // Padding interno de la card
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono de la aplicación
            Image(
                painter = painterResource(id = app.iconResId),
                contentDescription = "${app.name} icon",
                modifier = Modifier
                    .size(50.dp) // Tamaño del icono
                    .clip(RoundedCornerShape(12.dp)) // Redondea el icono también
            )

            Spacer(modifier = Modifier.width(16.dp)) // Espacio entre el icono y el texto

            // Información de la aplicación (nombre y descripción)
            Column(modifier = Modifier.weight(1f)) { // El Column toma el espacio restante
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.headlineSmall, // Estilo de texto del tema
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color.Black // Color del texto del nombre

                )
                Spacer(modifier = Modifier.height(4.dp)) // Espacio entre nombre y descripción
                Text(
                    text = app.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray, // Color del texto de la descripción
                    maxLines = 1, // Limita la descripción a 2 líneas
                    overflow = TextOverflow.Ellipsis // Añade puntos suspensivos si el texto es muy largo
                )

                Text(
                    text = "Actualizada: 12/05/2025",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray, // Color del texto de la descripción
                    maxLines = 1, // Limita la descripción a 2 líneas
                    overflow = TextOverflow.Ellipsis // Añade puntos suspensivos si el texto es muy largo
                )
                Spacer(modifier = Modifier.height(4.dp)) // Espacio entre nombre y descripción
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.End, // Asegura que los elementos internos de esta columna se alineen a la derecha
                    verticalArrangement = Arrangement.Center // Asegura que los elementos internos de esta columna se centren verticalmente
                )  {
                    Button(
                        onClick = { onClick(app) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF007aff)) // Color azul
                    ) {
                        Text("Ver Detalles")
                    }
                }
            }
        }
    }
}
