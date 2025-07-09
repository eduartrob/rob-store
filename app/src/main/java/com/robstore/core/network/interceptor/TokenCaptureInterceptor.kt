package com.robstore.core.network.interceptor

import com.robstore.core.store.local.DataStoreManager
import com.robstore.core.store.local.PreferenceKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class TokenCaptureInterceptor(
    private val dataStore: DataStoreManager
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        val authHeader = response.header("Authorization")

        if (!authHeader.isNullOrEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                dataStore.saveKey(PreferenceKeys.TOKEN, authHeader)
            }
        }

        return response
    }
}