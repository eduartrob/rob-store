package com.robstore.core.hardware.internet.domain.repository

import kotlinx.coroutines.flow.Flow

interface InternetConnectivityRepository {
    fun connectivityStatus(): Flow<Boolean>
}