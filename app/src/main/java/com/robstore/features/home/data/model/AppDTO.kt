package com.robstore.features.home.data.model

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

data class AppFilesResponseDTO(
    val message: String,
    @SerializedName("appFiles")
    val appFilesData: AppFilesDetailsDTO
)

data class AppDTO(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val description: String,
    val version: String,
    val developerId: String,
    val releaseDate: String,
    @SerializedName("__v")
    val versionField: Int? = null
)