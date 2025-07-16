package com.robstore.core.store.local.dataStore


import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore : DataStore<Preferences> by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    suspend fun saveKey(key: Preferences.Key<String>, value: String) {
        context.dataStore.edit { prefs -> prefs[key] = value }
    }

    fun getKey(key: Preferences.Key<String>): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[key]
        }
    }

    suspend fun deleteKey(key: Preferences.Key<String>) {
        context.dataStore.edit { prefs -> prefs.remove(key) }
    }


    fun getUserInformation(): Flow<UserProfileLocal> {
        return context.dataStore.data.map { prefs ->
            UserProfileLocal(
                name = prefs[PreferenceKeys.USER_NAME],
                email = prefs[PreferenceKeys.USER_EMAIL],
                phone = prefs[PreferenceKeys.USER_PHONE]
            )
        }
    }
    suspend fun saveUserInformation(name: String?, email: String?, phone: String?) {
        try {
            context.dataStore.edit { prefs ->
                name?.let { prefs[PreferenceKeys.USER_NAME] = it }
                email?.let { prefs[PreferenceKeys.USER_EMAIL] = it }
                phone?.let { prefs[PreferenceKeys.USER_PHONE] = it }
            }
            Log.d("DataStoreManager", "Información de usuario guardada.")
        } catch (e: Exception) {
            Log.e("DataStoreManager", "Error al guardar información de usuario: ${e.message}", e)
        }
    }






    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}

