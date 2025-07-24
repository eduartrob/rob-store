package com.robstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.robstore.core.navigation.AppNavigation

import com.robstore.features.authentication.login.di.AppModule

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        installSplashScreen()
        super.onCreate(savedInstanceState)

        WindowCompat.getInsetsController(window, window.decorView).let { insetsController ->
            insetsController.isAppearanceLightStatusBars = false
        }

        AppModule.init(applicationContext)

        setContent {
            AppNavigation()
        }
    }

}




