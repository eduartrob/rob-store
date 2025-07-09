package com.robstore.features.authentication.register.data.repository

import com.robstore.features.authentication.register.data.datasource.RegisterService
import com.robstore.features.authentication.register.data.model.RegisterRequest
import com.robstore.features.authentication.register.domain.model.Register
import com.robstore.features.authentication.register.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val registerService: RegisterService
): RegisterRepository {
    override suspend fun register(name: String, email: String, password: String, phone: String): Result<Register> {
        val registerRequest = RegisterRequest(name, email, password, phone)
        return try {
            val response = registerService.register(registerRequest)
            if (response.isSuccessful) {
                response.body()?.let { registerValidateDTO ->
                    Result.success(Register(message = registerValidateDTO.message))
                } ?: Result.failure(Exception("Respuesta exitosa pero cuerpo de datos nulo."))
            } else {
                Result.failure(Exception("Code fallido: ${response.code()}"))
            }
        }  catch (e: Exception){
            Result.failure(e)
        }
    }
}