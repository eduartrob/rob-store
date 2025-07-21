package com.robstore.features.authentication.register.domain.model

data class Register(
    val name: String,
    val email: String,
    val phone: String,
    val region: String?
)

