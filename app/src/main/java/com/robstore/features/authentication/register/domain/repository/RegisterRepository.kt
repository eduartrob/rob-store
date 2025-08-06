package com.robstore.features.authentication.register.domain.repository
import com.robstore.features.authentication.register.domain.model.Register

interface RegisterRepository {
    suspend fun register(name: String, email: String, password: String, phone: String, fireToken: String): Result<Register>
}