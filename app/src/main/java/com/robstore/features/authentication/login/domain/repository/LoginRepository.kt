package com.robstore.features.authentication.login.domain.repository

import com.robstore.features.authentication.login.domain.model.User

interface LoginRepository {
    suspend fun login(email: String, passwd: String, fireToken: String): Result<User>
}