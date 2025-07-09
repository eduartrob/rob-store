package com.robstore.features.profile.data.model

data class UpdateUserRequest(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val phone: String? = null
)
