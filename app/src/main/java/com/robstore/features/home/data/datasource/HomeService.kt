package com.robstore.features.home.data.datasource

import com.robstore.features.home.data.model.ImgProfileDTO
import retrofit2.Response
import retrofit2.http.GET

interface HomeService {
    @GET("/api/s3/get-image-profile")
    suspend fun getProfileImage(): Response<ImgProfileDTO>
}