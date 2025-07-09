package com.robstore.features.authentication.recoveryPassword.domain.repository

import com.robstore.features.authentication.recoveryPassword.data.model.RecoveryValidateDTO
import com.robstore.features.authentication.recoveryPassword.data.model.ResetPasswordDTO

interface RecoveryRepository {
    suspend fun recovery(email: String): Result<RecoveryValidateDTO>
    suspend fun validateCode(code: Int): Result<RecoveryValidateDTO>
    suspend fun resetPassword(code: Int, newPassword: String): Result<ResetPasswordDTO>
}