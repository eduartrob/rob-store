package com.robstore.core.network.interceptor

import okhttp3.logging.HttpLoggingInterceptor

fun provideLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}