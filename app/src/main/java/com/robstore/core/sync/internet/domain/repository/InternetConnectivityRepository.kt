package com.robstore.core.sync.internet.domain.repository

import kotlinx.coroutines.flow.Flow

interface InternetConnectivityRepository {
    fun connectivityStatus(): Flow<Boolean>
}