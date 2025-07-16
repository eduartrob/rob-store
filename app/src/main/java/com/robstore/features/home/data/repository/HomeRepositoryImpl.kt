package com.robstore.features.home.data.repository

import com.robstore.features.home.data.datasource.HomeService
import com.robstore.features.home.domain.model.Picture
import com.robstore.features.home.domain.repository.HomeRepository

class HomeRepositoryImpl(
    private val homeService: HomeService
): HomeRepository {
    override suspend fun getImageProfile(): Result<Picture> {
        return try {
            val response = homeService.getProfileImage()
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    Result.success(
                        Picture(
                            message = dto.message,
                            imgProfile = dto.fileUrl
                        )
                    )
                } ?: Result.failure(Exception("Respuesta exitosa pero cuerpo de datos nulo."))
            } else {
                Result.failure(
                    Exception("Error al obtener imagen de perfil: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}