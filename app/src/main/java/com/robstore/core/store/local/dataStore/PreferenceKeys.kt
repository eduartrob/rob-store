package com.robstore.core.store.local.dataStore

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKeys {
    val TOKEN = stringPreferencesKey("token")
    val USER_ID = stringPreferencesKey("user_id")
    val USER_NAME = stringPreferencesKey("user_name")
    val USER_EMAIL = stringPreferencesKey("user_email")
    val USER_PHONE = stringPreferencesKey("user_phone")
    val USER_REGION = stringPreferencesKey("user_region")
    val USER_PROFILE_PICTURE_URI = stringPreferencesKey("user_profile_picture_uri")
}