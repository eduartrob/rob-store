package com.robstore.features.home.domain.repository

import com.robstore.features.home.domain.model.App
import com.robstore.features.home.domain.model.Picture

interface HomeRepository {

    suspend fun getImageProfile(): Result<Picture>
    suspend fun getAllApps(): Result<List<App>>
    suspend fun getAppFiles(appId: String): Result<App>
}