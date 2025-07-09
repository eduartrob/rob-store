package com.robstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.robstore.core.navigation.AppNavigation

import com.robstore.features.authentication.login.di.AppModule

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.robstore.core.network.RetrofitHelper
import com.robstore.core.network.interceptor.AddTokenInterceptor
import com.robstore.core.network.interceptor.TokenCaptureInterceptor
import com.robstore.features.authentication.login.data.repository.TokenRepositoryImpl

import com.robstore.core.network.interceptor.provideLoggingInterceptor
import com.robstore.core.store.local.DataStoreManager


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




