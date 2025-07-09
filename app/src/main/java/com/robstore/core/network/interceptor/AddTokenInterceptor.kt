package com.robstore.core.network.interceptor

import android.util.Log
import com.robstore.core.store.local.DataStoreManager
import com.robstore.core.store.local.PreferenceKeys
import kotlinx.coroutines.flow.firstOrNull

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response


class AddTokenInterceptor(
    private val dataStore: DataStoreManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        val token = runBlocking {
            try {
                // <-- ¡CORRECCIÓN CLAVE AQUÍ! Usar .firstOrNull() para obtener el valor del Flow
                dataStore.getKey(PreferenceKeys.TOKEN).firstOrNull()
            } catch (e: Exception) {
                Log.e("AddTokenInterceptor", "Error obteniendo token de DataStore", e)
                null
            }
        }

        token?.let { rawToken ->
            // --- ¡CORRECCIÓN CLAVE AQUÍ! ---
            // Limpiamos el token si ya contiene el prefijo "Bearer "
            val cleanedToken = if (rawToken.startsWith("Bearer ", ignoreCase = true)) {
                rawToken.substring("Bearer ".length)
            } else {
                rawToken
            }

            // Si el token limpio no está vacío, lo añadimos al encabezado Authorization.
            if (cleanedToken.isNotEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $cleanedToken")
                Log.d("AddTokenInterceptor", "Token añadido a la petición: Bearer $cleanedToken") // Log del token real (limpio)
            } else {
                Log.d("AddTokenInterceptor", "Token limpio estaba vacío después de procesar.")
            }
        } ?: run {
            Log.d("AddTokenInterceptor", "No se encontró token en DataStore para añadir a la petición.")
        }

        return chain.proceed(requestBuilder.build())
    }
}