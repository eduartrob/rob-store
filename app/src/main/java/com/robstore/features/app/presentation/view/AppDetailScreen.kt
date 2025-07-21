package com.robstore.features.app.presentation.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.robstore.R
import com.robstore.features.app.domain.model.AppInfo
import com.robstore.features.home.domain.model.App
import com.robstore.features.home.presentation.viewModel.HomeViewModel


@Composable
fun AppDetailScreen(
    app: App, // Recibe el modelo de dominio App
    homeViewModel: HomeViewModel = viewModel(), // Inyecta el HomeViewModel
    onBack: () -> Unit
) {
    val colorGeneral = Color(0xFFf0f3f8)

    // Observa los estados de los archivos de la app seleccionada
    val selectedAppFiles by homeViewModel.selectedAppFiles.collectAsState()
    val appFilesLoading by homeViewModel.appFilesLoading.collectAsState()
    val appFilesError by homeViewModel.appFilesError.collectAsState()

    // Dispara la carga de los archivos de la app cuando la pantalla se componga
    LaunchedEffect(app.id) { // Usa app.id como key para que se recargue si cambia la app
        homeViewModel.fetchAppFiles(app.id)
    }

    val displayApp = selectedAppFiles ?: app

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
                        .offset(x = (-9).dp),
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // --- Carga del icono de la app ---
            Image(
                painter = rememberAsyncImagePainter(model = displayApp.filesDetails?.iconUrl), // Usa displayApp.filesDetails?.iconUrl
                contentDescription = "${displayApp.name} icon",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.LightGray), // Fondo para el icono
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = displayApp.name,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(48.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))

        // --- Mostrar carga, error o detalles de la app ---
        when {
            appFilesLoading -> {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            appFilesError != null -> {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Error al cargar detalles: ${appFilesError}",
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            else -> {
                // Contenido de los detalles de la app (rate, size, description)
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
                            text = displayApp.uiDetails?.rate.toString(), // Usa displayApp.uiDetails?.rate
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
                        text = displayApp.uiDetails?.size ?: "N/A", // Usa displayApp.uiDetails?.size
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Acción de instalar */ },
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
                            text = displayApp.description, // Usa displayApp.description
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.DarkGray
                        )
                    }
                }

                // TODO: Aquí podrías añadir un LazyRow para mostrar las screenshots
                if (!displayApp.filesDetails?.screenshots.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Capturas de pantalla:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Ejemplo de LazyRow para screenshots
                    // LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    //     items(displayApp.filesDetails.screenshots!!) { screenshotUrl ->
                    //         Image(
                    //             painter = rememberAsyncImagePainter(model = screenshotUrl),
                    //             contentDescription = "Screenshot",
                    //             modifier = Modifier
                    //                 .size(200.dp, 120.dp) // Ajusta el tamaño
                    //                 .clip(RoundedCornerShape(8.dp)),
                    //             contentScale = ContentScale.Crop
                    //         )
                    //     }
                    // }
                }
            }
        }
    }
}

//// --- Función de Previsualización ---
//@Preview(showBackground = true) // showBackground muestra un fondo, showSystemUi simula la barra de estado/navegación
//@Composable
//fun PreviewAppDetailScreen() {
//    MaterialTheme { // Siempre envuelve tus Previews en el tema de tu app
//        // Datos de ejemplo para la previsualización
//        val sampleApp = AppInfo("Facebook",
//            iconResId =  R.drawable.logo,  "Conecta con amigos y familiares.",
//            rate = 4.3, size = "23 MB"
//        )
//
//        AppDetailScreen(
//            app = sampleApp,
//            onBack = { /* No hace nada en Preview */ }
//        )
//    }
//}