package com.robstore.features.home.domain.model

import com.google.gson.annotations.SerializedName


data class AppFilesDetails(
    val iconUrl: String? = null,
    val appFileUrl: String? = null,
    val appFileSize: Long? = null,
    val appFileContentType: String? = null,
    val screenshots: List<String>? = null,
    val uploadedAt: String? = null
)


data class AppUIDetails(
    val rate: Double = 0.0,
    val size: String = "N/A"
)

data class App(
    val id: String,
    val name: String,
    val description: String,
    val version: String,
    val developerId: String,
    val releaseDate: String,

    val filesDetails: AppFilesDetails? = null,
    val uiDetails: AppUIDetails? = null
)