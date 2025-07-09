// En RetrofitHelper.kt
package com.robstore.core.network

import com.robstore.core.network.interceptor.AddTokenInterceptor
import com.robstore.core.network.interceptor.TokenCaptureInterceptor
import com.robstore.core.network.interceptor.provideLoggingInterceptor
import com.robstore.core.store.local.DataStoreManager
import com.robstore.features.authentication.login.data.datasource.LoginService
import com.robstore.features.authentication.recoveryPassword.data.datasource.RecoveryService
import com.robstore.features.authentication.register.data.datasource.RegisterService
import com.robstore.features.profile.data.datasource.ProfileService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitHelper {
    private const val BASE_URL = "https://store.eduartrob.xyz/"
    private const val TIMEOUT = 20L

    private lateinit var retrofitInstance: Retrofit
    private lateinit var dataStoreManagerInstance: DataStoreManager


    fun init(dataStoreManager: DataStoreManager, extraInterceptors: List<Interceptor> = emptyList()) {
        dataStoreManagerInstance = dataStoreManager
        val client = buildHttpClient(extraInterceptors)
        retrofitInstance = buildRetrofit(client)
    }

    private fun buildRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun buildHttpClient(extraInterceptors: List<Interceptor>): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .apply {
                extraInterceptors.forEach { addInterceptor(it) }
            }
            .build()
    }


    fun <T> getService(serviceClass: Class<T>): T {
        check(::retrofitInstance.isInitialized) { "RetrofitHelper debe ser inicializado antes de obtener servicios. Llama a init() primero." }
        return retrofitInstance.create(serviceClass)
    }

    fun getLoginService(): LoginService {
        return getService(LoginService::class.java)
    }

    fun getRecoveryService(): RecoveryService{
        return getService(RecoveryService::class.java)
    }

    fun getRegisterService(): RegisterService {
        return getService((RegisterService::class.java))
    }

    fun getProfileService(): ProfileService {
        return getService((ProfileService::class.java))
    }

}