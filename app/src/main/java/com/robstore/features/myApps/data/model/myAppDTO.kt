package com.robstore.features.myApps.data.model

import com.google.gson.annotations.SerializedName

data class AppFilesDetailsDTO(
    val appId: String? = null,
    val iconUrl: String? = null,
    val appFileUrl: String? = null,
    val appFileSize: Long? = null,
    val appFileContentType: String? = null,
    val screenshots: List<String>? = null,
    val uploadedAt: String? = null
)

data class AppUIDetailsDTO(
    val appFilesData: AppFilesDetailsDTO? = null
)

data class AppDTO(
    val _id: String,
    val name: String,
    val description: String,
    val version: String,
    val developerId: String,
    val releaseDate: String,
    val rate: Double,
)

data class CreateAppResponseDTO(
    val message: String,
    val app: AppDTO
)

data class GetDataFilesCreateAppResponseDTO(
    val message: String,
    val filesApp: AppFilesDetailsDTO
)