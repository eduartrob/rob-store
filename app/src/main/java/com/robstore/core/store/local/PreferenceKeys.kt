package com.robstore.core.store.local

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val TOKEN = stringPreferencesKey("token")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PHONE = stringPreferencesKey("user_phone")
}