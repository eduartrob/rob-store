package com.robstore.features.profile.data.datasource

import com.robstore.features.profile.data.model.ImageResponseDTO
import com.robstore.features.profile.data.model.LogoutDTO
import com.robstore.features.profile.data.model.UpdateUserRequest
import com.robstore.features.profile.data.model.UserUpdateDTO
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ProfileService {
    @PUT("api/users/update-user")
    suspend fun updateUser(@Body request: UpdateUserRequest): Response<UserUpdateDTO>

    @POST("api/users/logout")
    suspend fun logout(): Response<LogoutDTO>

    @Multipart
    @POST("api/s3/upload-image-profile")
    suspend fun uploadProfilePicture(@Part file: MultipartBody.Part): Response<ImageResponseDTO>



}