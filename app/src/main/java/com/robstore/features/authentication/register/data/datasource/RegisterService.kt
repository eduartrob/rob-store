package com.robstore.features.authentication.register.data.datasource


import com.robstore.features.authentication.register.data.model.RegisterRequest
import com.robstore.features.authentication.register.data.model.RegisterResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterService {
    @POST("api/users/sign-up")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponseDTO>
}

