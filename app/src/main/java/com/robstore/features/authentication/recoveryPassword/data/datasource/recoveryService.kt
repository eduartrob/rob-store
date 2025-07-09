package com.robstore.features.authentication.recoveryPassword.data.datasource

import com.robstore.features.authentication.recoveryPassword.data.model.RecoveryRequest
import com.robstore.features.authentication.recoveryPassword.data.model.RecoveryValidateDTO
import com.robstore.features.authentication.recoveryPassword.data.model.ResetPassword
import com.robstore.features.authentication.recoveryPassword.data.model.ResetPasswordDTO
import com.robstore.features.authentication.recoveryPassword.data.model.VerifyCode
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RecoveryService {
    @POST("api/users/forgot")
    suspend fun recovery(@Body request: RecoveryRequest): Response<RecoveryValidateDTO>

    @POST("api/users/verify-code")
    suspend fun verifyCode(@Body request: VerifyCode): Response<RecoveryValidateDTO>

    @POST("api/users/reset-password")
    suspend fun resetPassword(@Body request: ResetPassword): Response<ResetPasswordDTO>
}