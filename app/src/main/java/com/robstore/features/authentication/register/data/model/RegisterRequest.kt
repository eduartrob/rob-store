package com.robstore.features.authentication.register.data.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)
