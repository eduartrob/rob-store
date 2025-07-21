package com.robstore.core.hardware.location.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.robstore.core.hardware.location.domain.repository.LocationRepository
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import java.util.Locale
import kotlin.coroutines.resume


class LocationManager(private val context: Context) : LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    init {
        Log.d("LocationManager", "LocationManager inicializado.")
    }
    @SuppressLint("MissingPermission")
    override suspend fun getLastLocation(): Result<Location> = suspendCancellableCoroutine { continuation ->
        Log.d("LocationManager", "Intentando obtener la última ubicación conocida.")
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d("LocationManager", "Ubicación obtenida: Lat=${location.latitude}, Lon=${location.longitude}")
                    continuation.resume(Result.success(location))
                } else {
                    Log.w("LocationManager", "Última ubicación conocida es nula.")
                    continuation.resume(Result.failure(Exception("Última ubicación conocida no disponible.")))
                }
            }
            .addOnFailureListener { e: Exception ->
                Log.e("LocationManager", "Error al obtener la última ubicación: ${e.message}", e)
                continuation.resume(Result.failure(Exception("Error al obtener la última ubicación: ${e.message}")))
            }
            .addOnCanceledListener {
                Log.d("LocationManager", "Operación de ubicación cancelada.")
                continuation.cancel()
            }
    }

    override suspend fun getRegionFromLocation(location: Location): Result<String> {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                suspendCancellableCoroutine { continuation ->
                    geocoder.getFromLocation(location.latitude, location.longitude, 1) { list ->
                        continuation.resume(list)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                geocoder.getFromLocation(location.latitude, location.longitude, 1)
            }

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val region = address.countryName
                Log.d("LocationManager", "Región obtenida: $region")
                Result.success(region ?: "Región Desconocida")
            } else {
                Log.w("LocationManager", "No se encontraron direcciones para la ubicación: ${location.latitude}, ${location.longitude}")
                Result.failure(Exception("No se pudo determinar la región para la ubicación."))
            }
        } catch (e: IOException) {
            Log.e("LocationManager", "Error de red/IO al geocodificar: ${e.message}", e)
            Result.failure(Exception("Error de red al determinar la región: ${e.message}"))
        } catch (e: Exception) {
            Log.e("LocationManager", "Error inesperado al geocodificar: ${e.message}", e)
            Result.failure(Exception("Error al determinar la región: ${e.message}"))
        }
    }
}
