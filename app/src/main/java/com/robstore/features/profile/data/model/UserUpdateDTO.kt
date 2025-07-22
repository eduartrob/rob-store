package com.robstore.features.profile.data.model

data class UserUpdateDTO(
    val name: String,
    val email: String,
    val phone: String
)

data class LogoutDTO(
    val message: String
)

data class ImageResponseDTO(
    val message: String,
    val fileUrl: String?
)