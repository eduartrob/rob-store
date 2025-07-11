package com.robstore.features.authentication.login.data.model


data class UserDataDTO(
    val name: String,
    val email: String,
    val phone: String,
    val region: String?
)


data class LoginResponseDTO(
    val message: String,
    val data: UserDataDTO
)
