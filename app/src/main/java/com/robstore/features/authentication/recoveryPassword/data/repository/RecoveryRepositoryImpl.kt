package com.robstore.features.authentication.recoveryPassword.data.repository

import com.robstore.features.authentication.recoveryPassword.data.datasource.RecoveryService
import com.robstore.features.authentication.recoveryPassword.data.model.RecoveryRequest
import com.robstore.features.authentication.recoveryPassword.data.model.RecoveryValidateDTO
import com.robstore.features.authentication.recoveryPassword.data.model.ResetPassword
import com.robstore.features.authentication.recoveryPassword.data.model.ResetPasswordDTO
import com.robstore.features.authentication.recoveryPassword.data.model.VerifyCode
import com.robstore.features.authentication.recoveryPassword.domain.repository.RecoveryRepository

class RecoveryRepositoryImpl(
    private val recoveryService: RecoveryService
): RecoveryRepository {
    override suspend fun recovery(email: String): Result<RecoveryValidateDTO> {
        val recoveryRequest = RecoveryRequest(email)
        return try {
            val response = recoveryService.recovery(recoveryRequest)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Recovery fallido: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun validateCode(code: Int): Result<RecoveryValidateDTO> {
        val validateCode = VerifyCode(code)
        return try{
            val response = recoveryService.verifyCode(validateCode)
            if (response.isSuccessful && response.body() != null){
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Code fallido: ${response.code()}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun resetPassword(code: Int, newPassword: String): Result<ResetPasswordDTO>{
        val resetPassword = ResetPassword(code, newPassword)
        return try{
            val response = recoveryService.resetPassword(resetPassword)
            if (response.isSuccessful && response.body() != null){
                Result.success((response.body()!!))
            } else {
                Result.failure(Exception("Reset password failed: ${response.code()}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}