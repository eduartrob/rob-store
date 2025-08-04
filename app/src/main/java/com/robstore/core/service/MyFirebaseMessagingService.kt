package com.robstore.core.service


import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.robstore.core.store.local.dataStore.PreferenceKeys
import com.robstore.features.authentication.login.di.AppModule // Para acceder a DataStoreManager
import com.robstore.features.myApps.di.MyAppsModule.myAppsNotificationsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMsgService"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo FCM Token: $token")

        serviceScope.launch {
            try {
                val dataStoreManager = AppModule.getDataStoreManager()
                dataStoreManager.saveKey(PreferenceKeys.TOKEN_MYFIREBASE, token)
                Log.d(TAG, "FCM Token guardado en DataStore desde onNewToken: $token")
            } catch (e: Exception) {
                Log.e(TAG, "Error al guardar o enviar FCM token: ${e.message}", e)
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationTitle = remoteMessage.notification?.title
        val notificationBody = remoteMessage.notification?.body

        Log.d(TAG, "Mensaje de: ${remoteMessage.from}")
        if (notificationTitle != null) {
            myAppsNotificationsUseCase.infoData("$notificationTitle\n$notificationBody")
        }

    }
}