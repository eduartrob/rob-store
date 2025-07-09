package com.robstore.features.app.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robstore.R
import com.robstore.features.app.domain.model.AppInfo


@Composable
fun AppDetailScreen(
    app: AppInfo,
    onBack: () -> Unit // Se mantiene para regresar a la lista
) {
    var colorGeneral = Color(0xFFf0f3f8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFf0f3f8))
            .padding(horizontal = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    modifier = Modifier
                        .size(35.dp)
                        .offset(x = (-9).dp)
                    ,
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"

                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = app.iconResId),
                contentDescription = "${app.name} icon",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray) // Fondo para el icono
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = app.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier.weight(1f),

            )
            Spacer(Modifier.width(48.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
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
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFFFFD700)
                )
            }
            Text(
                text = "|",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 12.dp)
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
        Button(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp),
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2370ed),
                contentColor = Color.White
            )
        ) {
            Text(text = "Instalar", fontSize = 16.sp, fontWeight = FontWeight.Normal)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorGeneral
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

        // Spacer(modifier = Modifier.height(24.dp)) // Eliminado, ya no hay botones de acción

        // Botones de acción (Edit y Delete) eliminados de esta pantalla
        // Row(
        //     modifier = Modifier.fillMaxWidth(),
        //     horizontalArrangement = Arrangement.SpaceAround,
        //     verticalAlignment = Alignment.CenterVertically
        // ) {
        //     Button(onClick = { onEditClick(app) }) {
        //         Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
        //         Spacer(modifier = Modifier.width(8.dp))
        //         Text("Editar")
        //     }
        //     Button(
        //         onClick = { onDeleteClick(app) },
        //         colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        //     ) {
        //         Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
        //         Spacer(modifier = Modifier.width(8.dp))
        //         Text("Eliminar")
        //     }
        // }
    }
}

// --- Función de Previsualización ---
@Preview(showBackground = true) // showBackground muestra un fondo, showSystemUi simula la barra de estado/navegación
@Composable
fun PreviewAppDetailScreen() {
    MaterialTheme { // Siempre envuelve tus Previews en el tema de tu app
        // Datos de ejemplo para la previsualización
        val sampleApp = AppInfo("Facebook",
            iconResId =  R.drawable.logo,  "Conecta con amigos y familiares.",
            rate = 4.3, size = "23 MB"
        )

        AppDetailScreen(
            app = sampleApp,
            onBack = { /* No hace nada en Preview */ }
        )
    }
}