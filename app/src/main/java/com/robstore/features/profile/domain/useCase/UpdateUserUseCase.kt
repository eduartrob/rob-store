package com.robstore.features.profile.domain.useCase

import com.robstore.features.profile.domain.model.Logout
import com.robstore.features.profile.domain.model.UpdateUser
import com.robstore.features.profile.domain.repository.UpdateUserRepository

class UpdateUserUseCase(
    private val updateUserRepository: UpdateUserRepository
) {
    suspend operator fun invoke(name: String, email: String, phone: String): Result<UpdateUser>{
        return updateUserRepository.updateUser(name, email, phone)
    }

    suspend operator fun invoke(): Result<Logout>{
        return updateUserRepository.logout()
    }
}