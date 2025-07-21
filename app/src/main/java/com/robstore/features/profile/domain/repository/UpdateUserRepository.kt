package com.robstore.features.profile.domain.repository

import android.content.Context
import android.net.Uri
import com.robstore.features.profile.domain.model.ImageProfile
import com.robstore.features.profile.domain.model.Logout
import com.robstore.features.profile.domain.model.UpdateUser
import okhttp3.MultipartBody

interface UpdateUserRepository {
    suspend fun updateUser(name: String, email: String, phone: String): Result<UpdateUser>
    suspend fun logout(): Result<Logout>

    suspend fun uploadProfilePicture(imagePart: MultipartBody.Part): Result<ImageProfile>
}