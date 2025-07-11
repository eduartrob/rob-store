package com.robstore.features.authentication.login.domain.model

data class User(
    val name: String,
    val email: String,
    val phone: String,
    val region: String?
)


