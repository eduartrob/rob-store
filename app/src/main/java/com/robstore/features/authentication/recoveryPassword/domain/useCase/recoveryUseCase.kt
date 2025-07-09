package com.robstore.features.authentication.recoveryPassword.domain.useCase

import com.robstore.features.authentication.recoveryPassword.data.model.RecoveryValidateDTO
import com.robstore.features.authentication.recoveryPassword.data.model.ResetPasswordDTO
import com.robstore.features.authentication.recoveryPassword.domain.repository.RecoveryRepository

class RecoveryUseCase(
    private val recoveryRepository: RecoveryRepository
) {
    suspend operator fun invoke(email: String): Result<RecoveryValidateDTO>{
        return recoveryRepository.recovery(email)
    }

    suspend fun verifyRecoveryCode(code: Int): Result<RecoveryValidateDTO> {
        return recoveryRepository.validateCode(code)
    }

    suspend fun resetPassword(code: Int, newPassword: String): Result<ResetPasswordDTO>{
        return recoveryRepository.resetPassword(code, newPassword)
    }
}