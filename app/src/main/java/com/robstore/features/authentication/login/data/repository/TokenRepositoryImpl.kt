package com.robstore.features.authentication.login.data.repository
import com.robstore.core.store.local.dataStore.DataStoreManager
import com.robstore.core.store.local.dataStore.PreferenceKeys
import com.robstore.features.authentication.login.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow

class TokenRepositoryImpl(
    private val dataStoreManager: DataStoreManager
) : TokenRepository {
    override suspend fun getKey(): Flow<String?> = dataStoreManager.getKey(PreferenceKeys.TOKEN)
    override suspend fun saveKey(token: String) = dataStoreManager.saveKey(PreferenceKeys.TOKEN, value = token)

}