import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robstore.features.weather.presentation.viewModel.WeatherViewModel

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val darkBlueBackground = Color(0xFF1A237E)
    val weather by weatherViewModel.weather.collectAsState()

    val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    var hasLocationPermissionBeenRequested by remember { mutableStateOf(false) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            Log.d("WeatherDisplay", "Permiso de ubicación concedido.")
            Toast.makeText(context, "Permiso de ubicación concedido", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("WeatherDisplay", "Permiso de ubicación denegado por el usuario.")
            Toast.makeText(
                context,
                "Permiso de ubicación denegado. Algunas funciones podrían no estar disponibles.",
                Toast.LENGTH_LONG
            ).show()
        }
        hasLocationPermissionBeenRequested = true
    }

    // Solicita el permiso solo una vez
    LaunchedEffect(Unit) {
        if (!hasLocationPermissionBeenRequested) {
            locationPermissionLauncher.launch(locationPermission)
        }
    }

    IconButton(
        onClick = onBack,
        modifier = Modifier
            .padding(top = 16.dp, start = 8.dp) // Ajusta el padding
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Volver",
            tint = Color.White
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(darkBlueBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (weather != null) {
            Text(
                text = "🌤️ ${weather!!.temperature}°C",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ciudad de México",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Viento: ${weather!!.windspeed} km/h",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            )
            Text(
                text = "Dirección: ${weather!!.winddirection}°",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 18.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Actualizado: ${weather!!.time}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            )
        } else {
            Text(
                text = "Cargando clima...",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
            )
            CircularProgressIndicator()
        }
    }
}

