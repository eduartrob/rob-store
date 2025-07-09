package com.robstore.features.authentication.login.domain.useCase

import com.robstore.features.authentication.login.data.model.UserValidateDTO
import com.robstore.features.authentication.login.domain.repository.LoginRepository


class LoginUseCase(
    private val loginRepository: LoginRepository
) {

    suspend operator fun invoke(email: String, password: String): Result<UserValidateDTO> {
        return loginRepository.login(email, password)
    }
}
