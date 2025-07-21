package com.robstore.features.home.data.datasource

import com.robstore.features.home.data.model.AppDTO
import com.robstore.features.home.data.model.AppFilesResponseDTO
import com.robstore.features.home.data.model.ImgProfileDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface HomeService {
    @GET("/api/s3/get-image-profile")
    suspend fun getProfileImage(): Response<ImgProfileDTO>

    @GET("/api/apps/all")
    suspend fun getAllApps(): Response<List<AppDTO>>

    // --- getAppFiles: Usa AppFilesResponseDTO como respuesta de la API ---
    @GET("/api/s3/get-app-files/{appId}")
    suspend fun getAppFiles(@Path("appId") appId: String): Response<AppFilesResponseDTO>
}