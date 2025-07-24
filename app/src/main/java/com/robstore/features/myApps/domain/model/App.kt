package com.robstore.features.myApps.domain.model

data class AppFilesDetails(
    val iconUrl: String? = null,
    val appFileUrl: String? = null,
    val appFileSize: Long? = null,
    val screenshots: List<String>? = null,
    val uploadedAt: String? = null
)


data class AppUIDetails(
    val size: String? = null
)

data class App(
    val id: String,
    val name: String,
    val description: String,
    val version: String,
    val developerId: String,
    val releaseDate: String,
    val rate: Double,
    val filesDetails: AppFilesDetails? = null,
    val uiDetails: AppUIDetails? = null
)

data class DeleteApp(
    val message: String
)