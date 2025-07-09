package com.robstore.features.authentication.login.data.repository
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.robstore.core.store.local.DataStoreManager
import com.robstore.core.store.local.PreferenceKeys
import com.robstore.features.authentication.login.domain.repository.TokenRepository
import kotlinx.coroutines.flow.Flow

class TokenRepositoryImpl(
    private val dataStoreManager: DataStoreManager
) : TokenRepository {
    override suspend fun getKey(): Flow<String?> = dataStoreManager.getKey(PreferenceKeys.TOKEN)
    override suspend fun saveKey(token: String) = dataStoreManager.saveKey(PreferenceKeys.TOKEN, value = token)

}