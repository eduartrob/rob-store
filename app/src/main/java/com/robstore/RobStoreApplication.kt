package com.robstore

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp // ¡Importa esto!
// Ya no necesitas importar FirebaseMessaging aquí directamente
import com.robstore.features.authentication.login.di.AppModule

class RobStoreApplication : Application() {

    private val TAG = "RobStoreApplication"

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: Iniciando RobStoreApplication...")

        // *******************************************************************
        // *** 1. ESTO DEBE SER LA ÚNICA LLAMADA RELACIONADA CON FIREBASE DIRECTAMENTE AQUÍ ***
        // *******************************************************************
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "FirebaseApp inicializado correctamente.")
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error al inicializar FirebaseApp: ${e.message}. Puede que ya esté inicializado o haya un problema de configuración.")
        }

        // *******************************************************************
        // *** 2. Llama a la inicialización de tu AppModule después de FirebaseApp ***
        // *** Asegúrate de que AppModule.init() NO intente obtener el token de FCM directamente ***
        // *******************************************************************
        AppModule.init(applicationContext)
        Log.d(TAG, "AppModule inicializado correctamente.")

        // *******************************************************************
        // *** NO poner FirebaseMessaging.getInstance().token.addOnCompleteListener aquí ***
        // *** NI dentro de AppModule.init() ***
        // *******************************************************************

        Log.d(TAG, "Application onCreate finalizado.")
    }
}