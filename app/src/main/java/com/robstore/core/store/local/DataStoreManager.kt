package com.robstore.core.store.local


import android.content.Context
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

    suspend fun getKey(token: Preferences.Key<String>): Flow<String?> {
        return context.dataStore.data.map { prefs ->
            prefs[PreferenceKeys.TOKEN]
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
    suspend fun saveUserInformation(name: String?, email: String?, phone: String?){
        context.dataStore.edit { prefs ->
            name?.let { prefs[PreferenceKeys.USER_NAME] = it }
            email?.let { prefs[PreferenceKeys.USER_EMAIL] = it }
            phone?.let { prefs[PreferenceKeys.USER_PHONE] = it }
        }
    }




    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}

