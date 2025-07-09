package com.robstore.features.authentication.recoveryPassword.data.model

data class RecoveryRequest(
    val email: String
)

data class VerifyCode(
    val codeVerification: Int
)

data class ResetPassword(
    val codeVerification: Int,
    val newPassword: String
)