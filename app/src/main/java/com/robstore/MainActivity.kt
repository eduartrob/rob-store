package com.robstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.robstore.core.navigation.AppNavigation

import com.robstore.features.authentication.login.di.AppModule

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        AppModule.init(applicationContext)

        setContent {
            AppNavigation()
        }
    }

//    override fun onDestroy() {
//        super.onDestroy()
//    }
}




