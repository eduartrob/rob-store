package com.robstore.features.authentication.login.domain.repository

import com.robstore.features.authentication.login.data.model.UserValidateDTO


interface LoginRepository {
    suspend fun login(email: String, passwd: String): Result<UserValidateDTO>
}