package com.robstore.features.profile.domain.repository

import com.robstore.features.profile.domain.model.Logout
import com.robstore.features.profile.domain.model.UpdateUser

interface UpdateUserRepository {
    suspend fun updateUser(name: String, email: String, phone: String): Result<UpdateUser>
    suspend fun logout(): Result<Logout>
}