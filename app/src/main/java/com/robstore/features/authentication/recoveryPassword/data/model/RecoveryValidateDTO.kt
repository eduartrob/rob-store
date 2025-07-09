package com.robstore.features.authentication.recoveryPassword.data.model

data class RecoveryValidateDTO(
    val message: String,
    val validation: Boolean
)

data class ResetPasswordDTO(
    val message: String
)
