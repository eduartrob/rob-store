package com.robstore.features.profile.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.robstore.features.authentication.register.domain.model.Register
import com.robstore.features.profile.data.datasource.ProfileService
import com.robstore.features.profile.data.model.LogoutDTO
import com.robstore.features.profile.data.model.UpdateUserRequest
import com.robstore.features.profile.domain.model.ImageProfile
import com.robstore.features.profile.domain.model.Logout
import com.robstore.features.profile.domain.model.UpdateUser
import com.robstore.features.profile.domain.repository.UpdateUserRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class UpdateUserRepositoryImpl(
    private val profileService: ProfileService
): UpdateUserRepository  {
    override suspend fun updateUser(name: String, email: String, phone: String): Result<UpdateUser> {
        val request = UpdateUserRequest(name = name, email = email, phone = phone)
        return try {
            val response = profileService.updateUser(request)
            if (response.isSuccessful) {
                response.body()?.let { responseDTO ->
                    Result.success(UpdateUser(
                        name = responseDTO.name,
                        email = responseDTO.email,
                        phone = responseDTO.phone,
                    ))
                } ?: Result.failure(Exception("Respuesta exitosa pero cuerpo de datos nulo."))
            } else {
                Result.failure(Exception("Update user fallido: ${response.code()}"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Logout>{
        return try {
            val response = profileService.logout()
            if (response.isSuccessful){
                response.body()?.let { LogoutDTO ->
                    Result.success(Logout(message = LogoutDTO.message))
                } ?: Result.failure(Exception("Respuesta exitosa pero cuerpo de datos nulo."))
            } else {
                Result.failure(HttpException(response))
            }
        }  catch (e: Exception){
            Result.failure(e)
        }
    }

    override suspend fun uploadProfilePicture(imageUri: Uri, context: Context): Result<ImageProfile> {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val imageBytes = inputStream?.readBytes()
            inputStream?.close()

            if (imageBytes == null) {
                return Result.failure(Exception("No se pudieron leer los bytes de la imagen."))
            }

            val requestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())

            val imagePart = MultipartBody.Part.createFormData("file", "profile_picture.jpg", requestBody)


            Log.d("UpdateUserRepositoryImpl", "Subiendo imagen: URI=$imageUri, Bytes=${imageBytes.size}")

            val response = profileService.uploadProfilePicture(imagePart)

            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Log.d("UpdateUserRepositoryImpl", "Imagen subida con éxito. DTO: $dto")
                    Result.success(ImageProfile(
                        message = dto.message,
                        url = dto.fileUrl
                    ))
                } ?: Result.failure(Exception("Respuesta exitosa pero cuerpo de datos nulo para subir foto."))
            } else {
                Result.failure(Exception("Update user fallido: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


