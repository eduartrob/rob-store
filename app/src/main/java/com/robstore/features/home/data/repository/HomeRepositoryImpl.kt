package com.robstore.features.home.data.repository

import com.robstore.features.home.data.datasource.HomeService
import com.robstore.features.home.domain.model.App
import com.robstore.features.home.domain.model.AppFilesDetails
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


    override suspend fun getAllApps(): Result<List<App>> {
        return try {
            val response = homeService.getAllApps()
            if (response.isSuccessful) {
                response.body()?.let { appDtoList ->
                    val appList = appDtoList.map { dto ->
                        App(
                            id = dto.id,
                            name = dto.name,
                            description = dto.description,
                            version = dto.version,
                            developerId = dto.developerId,
                            releaseDate = dto.releaseDate,
                            rate = dto.rate,
                        )
                    }
                    Result.success(appList)
                } ?: Result.failure(Exception("Respuesta exitosa pero cuerpo de datos nulo para todas las apps."))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(
                    Exception("Error al obtener todas las apps: ${response.code()} - ${errorBody ?: response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppFiles(appId: String): Result<App> {
        return try {
            val response = homeService.getAppFiles(appId) // Llama al HomeService
            if (response.isSuccessful) {
                response.body()?.let { appFilesResponseDTO ->
                    val appFilesData = appFilesResponseDTO.appFilesData // Esto es un AppFilesDetailsDTO

                    // Mapea los datos del DTO (AppFilesDetailsDTO) a tu modelo de dominio (AppFilesDetails)
                    val filesDetails = AppFilesDetails(
                        iconUrl = appFilesData.iconUrl,
                        appFileUrl = appFilesData.appFileUrl,
                        appFileSize = appFilesData.appFileSize,
                        appFileContentType = appFilesData.appFileContentType,
                        screenshots = appFilesData.screenshots,
                        uploadedAt = appFilesData.uploadedAt
                    )

                    Result.success(
                        App(
                            id = appFilesData.appId ?: appId, // Usa el appId del DTO o el pasado si es nulo
                            name = "",
                            description = "",
                            version = "",
                            developerId = "",
                            releaseDate = "",
                            filesDetails = filesDetails,
                            uiDetails = null,
                            rate = 0.0
                        )
                    )
                } ?: Result.failure(Exception("Respuesta exitosa pero cuerpo de datos nulo para archivos de app."))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(
                    Exception("Error al obtener archivos de la app: ${response.code()} - ${errorBody ?: response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}