package com.robstore.features.home.domain.repository

import com.robstore.features.home.domain.model.Picture

interface HomeRepository {
    suspend fun getImageProfile(): Result<Picture>
}