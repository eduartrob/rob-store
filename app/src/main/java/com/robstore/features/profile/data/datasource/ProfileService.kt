package com.robstore.features.profile.data.datasource

import com.robstore.features.profile.data.model.LogoutDTO
import com.robstore.features.profile.data.model.UpdateUserRequest
import com.robstore.features.profile.data.model.UserUpdateDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface ProfileService {
    @PUT("api/users/update-user")
    suspend fun updateUser(@Body request: UpdateUserRequest): Response<UserUpdateDTO>

    @POST("api/users/logout")
    suspend fun logout(): Response<LogoutDTO>
}