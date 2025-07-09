package com.robstore.features.authentication.register.domain.useCase

import com.robstore.features.authentication.register.domain.model.Register
import com.robstore.features.authentication.register.domain.repository.RegisterRepository

class RegisterUseCase(
    private val registerRepository: RegisterRepository
) {
    suspend operator fun invoke(name: String, email: String, password: String, phone: String): Result<Register> {
        return registerRepository.register(name, email, password, phone)
    }
}