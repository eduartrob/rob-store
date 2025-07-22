package com.robstore.features.myApps.data.repository

import android.util.Log
import com.robstore.features.home.data.model.AppFilesResponseDTO
import com.robstore.features.myApps.data.datasource.MyAppsService
import com.robstore.features.myApps.data.model.UpdateApp
import com.robstore.features.myApps.domain.model.App // Importa tu modelo de dominio App (desde home.domain.model)
import com.robstore.features.myApps.domain.model.AppFilesDetails // Importa AppFilesDetails (desde home.domain.model)
import com.robstore.features.myApps.domain.model.AppUIDetails // Importa AppUIDetails (desde home.domain.model)
import com.robstore.features.myApps.domain.repository.MyAppsRepository // Importa la interfaz
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response

class MyAppsRepositoryImpl(
    private val myAppsService: MyAppsService,
) : MyAppsRepository {

    override suspend fun getMyApps(): Result<List<App>> {
        return try {
            val response = myAppsService.getMyApps()
            if (response.isSuccessful) {
                response.body()?.let { myAppDtoList ->
                    val enrichedMyApps = mutableListOf<App>()

                    for (myAppDto in myAppDtoList) {
                        val appFilesResult = getAppFiles(myAppDto._id)

                        if (appFilesResult.isSuccess) {
                            val appWithFiles = appFilesResult.getOrNull()
                            if (appWithFiles != null) {
                                enrichedMyApps.add(
                                    App(
                                        id = myAppDto._id,
                                        name = myAppDto.name,
                                        description = myAppDto.description,
                                        version = myAppDto.version,
                                        developerId = myAppDto.developerId,
                                        releaseDate = myAppDto.releaseDate,
                                        rate = myAppDto.rate,
                                        filesDetails = appWithFiles.filesDetails,
                                        uiDetails = AppUIDetails()
                                    )
                                )
                            } else {
                                enrichedMyApps.add(
                                    App(
                                        id = myAppDto._id,
                                        name = myAppDto.name,
                                        description = myAppDto.description,
                                        version = myAppDto.version,
                                        developerId = myAppDto.developerId,
                                        releaseDate = myAppDto.releaseDate,
                                        rate = myAppDto.rate, // Mantiene rate de la app básica
                                        filesDetails = null, // Sin detalles de archivos
                                        uiDetails = null
                                    )
                                )
                                Log.w("MyAppsRepositoryImpl", "No se encontraron archivos para mi app: ${myAppDto.name} (${myAppDto._id})")
                            }
                        } else {
                            enrichedMyApps.add(
                                App(
                                    id = myAppDto._id,
                                    name = myAppDto.name,
                                    description = myAppDto.description,
                                    version = myAppDto.version,
                                    developerId = myAppDto.developerId,
                                    releaseDate = myAppDto.releaseDate,
                                    rate = myAppDto.rate, // Mantiene rate de la app básica
                                    filesDetails = null, // Sin detalles de archivos
                                    uiDetails = AppUIDetails()
                                )
                            )
                            Log.e("MyAppsRepositoryImpl", "Error al cargar archivos para mi app ${myAppDto.name} (${myAppDto._id}): ${appFilesResult.exceptionOrNull()?.message}", appFilesResult.exceptionOrNull())
                        }
                    }
                    Result.success(enrichedMyApps)
                } ?: Result.failure(Exception("Respuesta exitosa pero cuerpo de datos nulo para mis apps."))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(
                    Exception("Error al obtener mis apps: ${response.code()} - ${errorBody ?: response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAppFiles(appId: String): Result<App> {
        return try {
            val response = myAppsService.getAppFiles(appId)
            if (response.isSuccessful) {
                response.body()?.let { appFilesResponseDTO: AppFilesResponseDTO ->
                    val filesDetails = AppFilesDetails(
                        iconUrl = appFilesResponseDTO.appFilesData.iconUrl,
                        appFileUrl = appFilesResponseDTO.appFilesData.appFileUrl,
                        appFileSize = appFilesResponseDTO.appFilesData.appFileSize,
                        screenshots = appFilesResponseDTO.appFilesData.screenshots,
                        uploadedAt = appFilesResponseDTO.appFilesData.uploadedAt
                    )

                    Result.success(
                        App(
                            id = appFilesResponseDTO.appFilesData.appId ?: appId,
                            name = "",
                            description = "",
                            version = "",
                            developerId = "",
                            releaseDate = "",
                            rate = 0.0,
                            filesDetails = filesDetails,
                            uiDetails = null
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



    override suspend fun updateApp(
        updatedApp: App,
        iconBytes: ByteArray?,
        apkBytes: ByteArray?,
        screenshotBytesList: List<ByteArray>
    ): Result<App> {
        return try {
            val updateMetadataResult = updatedApp.id?.let {
                myAppsService.updateAppMetadata(
                    appId = it,
                    request = UpdateApp(
                        name = updatedApp.name,
                        description = updatedApp.description,
                        version = updatedApp.version,
                        releaseDate = null
                    )
                )
            }

            if (updateMetadataResult != null) {
                if (!updateMetadataResult.isSuccessful) {
                    val errorBody = updateMetadataResult.errorBody()?.string()
                    return Result.failure(
                        Exception("Error al actualizar metadatos de la app: ${updateMetadataResult.code()} - ${errorBody ?: updateMetadataResult.message()}")
                    )
                }
            }

            if (iconBytes != null || apkBytes != null || screenshotBytesList.isNotEmpty()) {
                val iconPart = iconBytes?.let {
                    MultipartBody.Part.createFormData("icon", "icon.jpg", it.toRequestBody("image/jpeg".toMediaTypeOrNull()))
                }
                val apkPart = apkBytes?.let {
                    MultipartBody.Part.createFormData("appFile", "app.apk", it.toRequestBody("application/vnd.android.package-archive".toMediaTypeOrNull()))
                }
                val screenshotParts = screenshotBytesList.mapIndexed { index, bytes ->
                    MultipartBody.Part.createFormData("screenshots", "screenshot_$index.jpg", bytes.toRequestBody("image/jpeg".toMediaTypeOrNull()))
                }

                val uploadFilesResult = updatedApp.id?.let {
                    myAppsService.uploadAppFiles(
                        appId = it.toRequestBody("text/plain".toMediaType()),
                        icon = iconPart,
                        appFile = apkPart,
                        screenshots = screenshotParts
                    )
                }

//                if (!uploadFilesResult.isSuccessful) {
//                    val errorBody = uploadFilesResult?.errorBody()?.string()
//                    if (uploadFilesResult != null) {
//                        return Result.failure(
//                            Exception("Error al subir archivos de la app: ${uploadFilesResult.code()} - ${errorBody ?: uploadFilesResult.message()}")
//                        )
//                    }
//                }
            }

            Result.success(updatedApp)

        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos en la solicitud."
                404 -> "Aplicación no encontrada."
                413 -> "Archivo demasiado grande."
                else -> "Error del servidor: ${e.code()} - ${e.message()}"
            }
            Log.e("MyAppsRepositoryImpl", "HttpException al actualizar app: $errorMessage", e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("MyAppsRepositoryImpl", "Excepción al actualizar app: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun createApp(updatedApp: App, iconBytes: ByteArray?, apkBytes: ByteArray?, screenshotBytesList: List<ByteArray>): Result<App> {
        return try {
            val createAppRequest = UpdateApp(
                name = updatedApp.name,
                description = updatedApp.description,
                version = updatedApp.version,
                releaseDate = updatedApp.releaseDate
            )
            val createMetadataResponse = myAppsService.createAppMetadata(createAppRequest)
//

            Log.d("MyAppsRepositoryImpl", "createMetadataResponse: ${createMetadataResponse.body()?.app?._id}")
            // --- FIN LOG ---

            if (!createMetadataResponse.isSuccessful) {
                val errorBody = createMetadataResponse.errorBody()?.string()
                return Result.failure(
                    Exception("Error al crear metadatos de la app: ${createMetadataResponse.code()} - ${errorBody ?: createMetadataResponse.message()}")
                )
            }

            val createdAppDTO = createMetadataResponse.body()
                ?: return Result.failure(Exception("Respuesta nula al crear metadatos de la app."))

            val newAppId = createdAppDTO.app._id



            if (iconBytes == null || apkBytes == null || screenshotBytesList.isEmpty()) {
                return Result.failure(IllegalArgumentException("Icono, APK y al menos una captura de pantalla son obligatorios para la nueva aplicación."))
            }
            if (newAppId.isBlank()) {
                return Result.failure(Exception("El ID de la app creada está vacío"))
            }

            val appIdPart = newAppId.toRequestBody("text/plain".toMediaType())

            val iconPart = iconBytes.takeIf { it.isNotEmpty() }?.let {
                MultipartBody.Part.createFormData(
                    "icon", "icon.jpg", it.toRequestBody("image/jpeg".toMediaType())
                )
            }

            val apkPart = apkBytes.takeIf { it.isNotEmpty() }?.let {
                MultipartBody.Part.createFormData(
                    "appFile", "app.apk", it.toRequestBody("application/vnd.android.package-archive".toMediaType())
                )
            }

            val screenshotParts = screenshotBytesList
                .filter { it.isNotEmpty() }
                .mapIndexed { index, bytes ->
                    MultipartBody.Part.createFormData(
                        "screenshots", "screenshot_$index.jpg", bytes.toRequestBody("image/jpeg".toMediaType())
                    )
                }

            val uploadFilesResponse = myAppsService.uploadAppFiles(
                appId = appIdPart,
                icon = iconPart,
                appFile = apkPart,
                screenshots = screenshotParts.ifEmpty { null }
            )


            val filesDetails = uploadFilesResponse.body()?.filesApp?.let {
                AppFilesDetails(
                    iconUrl = it.iconUrl,
                    appFileUrl = it.appFileUrl,
                    appFileSize = it.appFileSize,
                    screenshots = it.screenshots,
                    uploadedAt = it.uploadedAt
                )
            }
            Result.success(updatedApp.copy(id = newAppId, filesDetails = filesDetails))

        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos en la solicitud de creación."
                413 -> "Archivo demasiado grande."
                else -> "Error del servidor al crear/subir archivos (${e.code()}): ${e.message()}"
            }
            Log.e("MyAppsRepositoryImpl", "HttpException al añadir app: $errorMessage", e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("MyAppsRepositoryImpl", "Excepción al añadir app: ${e.message}", e)
            Result.failure(e)
        }
    }
}
