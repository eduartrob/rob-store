package com.robstore.features.authentication.login.domain.repository

import kotlinx.coroutines.flow.Flow

interface TokenRepository {
    suspend fun getKey(): Flow<String?>
    suspend fun saveKey(token: String)
}