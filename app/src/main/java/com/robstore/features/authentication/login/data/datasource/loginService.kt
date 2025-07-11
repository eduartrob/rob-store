package com.robstore.features.authentication.login.data.datasource

import com.robstore.features.authentication.login.data.model.LoginRequest
import com.robstore.features.authentication.login.data.model.LoginResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService{
    @POST("api/users/sign-in")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponseDTO>
}