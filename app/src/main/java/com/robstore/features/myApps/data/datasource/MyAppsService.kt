package com.robstore.features.myApps.data.datasource


import com.robstore.features.home.data.model.AppFilesResponseDTO
import com.robstore.features.myApps.data.model.AppDTO
import com.robstore.features.myApps.data.model.CreateAppResponseDTO
import com.robstore.features.myApps.data.model.GetDataFilesCreateAppResponseDTO
import com.robstore.features.myApps.data.model.UpdateApp
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface MyAppsService {

    @GET("/api/apps/my-applications")
    suspend fun getMyApps(): Response<List<AppDTO>>

    @GET("/api/s3/get-app-files/{appId}")
    suspend fun getAppFiles(@Path("appId") appId: String): Response<AppFilesResponseDTO>

    @PUT("api/apps/update/{appId}")
    suspend fun updateAppMetadata(@Path("appId") appId: String, @Body request: UpdateApp): Response<AppDTO>

    @POST("api/apps/create")
    suspend fun createAppMetadata(
        @Body request: UpdateApp ): Response<CreateAppResponseDTO>


    @Multipart
    @POST("api/s3/upload-app-files")
    suspend fun uploadAppFiles(
        @Part("appId") appId: RequestBody,
        @Part icon: MultipartBody.Part?,
        @Part appFile: MultipartBody.Part?,
        @Part screenshots: List<MultipartBody.Part>?,
        @Part screenshotsToKeep: MultipartBody.Part?
    ): Response<GetDataFilesCreateAppResponseDTO>
}