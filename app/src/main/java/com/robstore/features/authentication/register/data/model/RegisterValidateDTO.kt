package com.robstore.features.authentication.register.data.model

data class UserDataDTO(
    val name: String,
    val email: String,
    val phone: String,
    val region: String?
)


data class RegisterResponseDTO(
    val message: String,
    val data: UserDataDTO
)
