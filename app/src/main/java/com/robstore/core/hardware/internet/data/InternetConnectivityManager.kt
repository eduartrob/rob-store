package com.robstore.core.hardware.internet.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.robstore.core.hardware.internet.domain.repository.InternetConnectivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InternetConnectivityManager(
    private val context: Context
) : InternetConnectivityRepository {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    // Un MutableStateFlow para mantener el estado actual de la conectividad
    private val _isConnected = MutableStateFlow(false)

    init {
        // Inicializa el estado al crear la instancia
        _isConnected.value = isConnectedToInternet()
        Log.d("InternetConnectivityMgr", "Estado inicial de conectividad: ${_isConnected.value}")

        // Registra el NetworkCallback para escuchar cambios
        registerNetworkCallback()
    }

    /**
     * @see InternetConnectivityRepository.isConnectedToInternet
     */
    fun isConnectedToInternet(): Boolean {
        if (connectivityManager == null) {
            Log.e("InternetConnectivityMgr", "ConnectivityManager es nulo. No se pudo verificar la conectividad.")
            return false
        }

        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork == null) {
            Log.d("InternetConnectivityMgr", "No hay red activa.")
            return false
        }

        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        if (capabilities == null) {
            Log.d("InternetConnectivityMgr", "No se pudieron obtener las capacidades de la red activa.")
            return false
        }

        // Verifica si la red activa tiene capacidad para INTERNET y si está VALIDATED (acceso real a Internet)
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        val isConnected = hasInternet && isValidated
        //Log.d("InternetConnectivityMgr", "Verificación manual: Internet=$hasInternet, Validada=$isValidated -> Conectado=$isConnected")
        return isConnected
    }

    /**
     * @see InternetConnectivityRepository.connectivityStatus
     */
    override fun connectivityStatus(): Flow<Boolean> = _isConnected.asStateFlow()

    /**
     * Registra un NetworkCallback para escuchar los cambios en la conectividad de la red.
     */
    private fun registerNetworkCallback() {
        if (connectivityManager == null) {
            Log.e("InternetConnectivityMgr", "No se pudo registrar NetworkCallback: ConnectivityManager es nulo.")
            return
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Cuando una red con capacidad de Internet está disponible
                val currentStatus = isConnectedToInternet()
                if (_isConnected.value != currentStatus) {
                    _isConnected.value = currentStatus
                    Log.d("InternetConnectivityMgr", "Network available. New status: ${_isConnected.value}")
                }
            }

            override fun onLost(network: Network) {
                // Cuando una red con capacidad de Internet se pierde
                val currentStatus = isConnectedToInternet()
                if (_isConnected.value != currentStatus) {
                    _isConnected.value = currentStatus
                    Log.d("InternetConnectivityMgr", "Network lost. New status: ${_isConnected.value}")
                }
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                // Cuando las capacidades de la red cambian (ej. pierde/gana acceso a Internet)
                val currentStatus = isConnectedToInternet()
                if (_isConnected.value != currentStatus) {
                    _isConnected.value = currentStatus
                    Log.d("InternetConnectivityMgr", "Network capabilities changed. New status: ${_isConnected.value}")
                }
            }
        }

        // Registra el callback. Asegúrate de que este callback se desregistre cuando ya no sea necesario
        // (ej. en el onCleared de un ViewModel si se inyecta directamente, o en un ciclo de vida de un servicio).
        // Para una aplicación de larga duración, es mejor que un servicio o un singleton de DI lo gestione.
        try {
            connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
            Log.d("InternetConnectivityMgr", "NetworkCallback registrado.")
        } catch (e: SecurityException) {
            Log.e("InternetConnectivityMgr", "Permiso de red denegado para NetworkCallback: ${e.message}", e)
            // Asegúrate de que ACCESS_NETWORK_STATE esté en AndroidManifest.xml
        }
    }
}