package com.robstore.features.myApps.data.repository

import android.util.Log
import com.google.gson.Gson
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
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.nio.charset.StandardCharsets

class MyAppsRepositoryImpl(
    private val myAppsService: MyAppsService,
) : MyAppsRepository {

    private val gson = Gson()


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
        screenshotBytesList: List<ByteArray>,
        screenshotsToKeepUrls: List<String> // Esta es la lista ya filtrada y consolidada del ViewModel
    ): Result<App> {
        return try {
            val appIdToUpdate = updatedApp.id

            // 1. Actualizar metadatos
            val updateMetadataResponse = myAppsService.updateAppMetadata(
                appId = appIdToUpdate,
                request = UpdateApp(
                    name = updatedApp.name,
                    description = updatedApp.description,
                    version = updatedApp.version,
                    releaseDate = updatedApp.releaseDate
                )
            )

            if (!updateMetadataResponse.isSuccessful) {
                val errorBody = updateMetadataResponse.errorBody()?.string()
                return Result.failure(
                    Exception("Error al actualizar metadatos de la app: ${updateMetadataResponse.code()} - ${errorBody ?: updateMetadataResponse.message()}")
                )
            }
            Log.d("MyAppsRepositoryImpl", "Metadatos actualizados con éxito para la app: ${updatedApp.name}")


            // 2. Preparar partes de archivos
            val iconPart = iconBytes?.takeIf { it.isNotEmpty() }?.let {
                Log.d("MyAppsRepositoryImpl", "Preparando parte del icono para subir.")
                MultipartBody.Part.createFormData("icon", "icon.jpg", it.toRequestBody("image/jpeg".toMediaType()))
            } ?: run {
                Log.d("MyAppsRepositoryImpl", "No hay icono nuevo o modificado para subir.")
                null
            }

            val apkPart = apkBytes?.takeIf { it.isNotEmpty() }?.let {
                Log.d("MyAppsRepositoryImpl", "Preparando parte del APK para subir.")
                MultipartBody.Part.createFormData("appFile", "app.apk", it.toRequestBody("application/vnd.android.package-archive".toMediaType()))
            } ?: run {
                Log.d("MyAppsRepositoryImpl", "No hay APK nuevo o modificado para subir.")
                null
            }

            val screenshotParts = screenshotBytesList
                .filter { it.isNotEmpty() }
                .mapIndexed { index, bytes ->
                    Log.d("MyAppsRepositoryImpl", "Preparando nueva captura de pantalla #${index} para subir.")
                    MultipartBody.Part.createFormData("screenshots", "screenshot_$index.jpg", bytes.toRequestBody("image/jpeg".toMediaType()))
                }

            // Preparar la parte para las URLs de capturas de pantalla a mantener
            // Esta parte SIEMPRE debe enviarse, incluso si está vacía, para indicarle al backend
            // qué URLs de capturas de pantalla (ya existentes) debe conservar.
            val jsonUrlsToKeep = gson.toJson(screenshotsToKeepUrls)
            Log.d("MyAppsRepositoryImpl", "screenshotsToKeep JSON enviado: $jsonUrlsToKeep")

            val screenshotsToKeepPart = MultipartBody.Part.createFormData(
                "screenshotsToKeep",
                null, // null como filename es para una parte de texto/JSON
                jsonUrlsToKeep.toRequestBody("application/json".toMediaType())
            )

            // 3. Subir archivos si hay nuevos archivos O si hay una lista de URLs a mantener
            // (La lista de URLs a mantener SIEMPRE se envía)
            if (iconPart != null || apkPart != null || screenshotParts.isNotEmpty() || screenshotsToKeepPart != null) {
                Log.d("MyAppsRepositoryImpl", "Realizando llamada a la API de subida de archivos para la app: ${updatedApp.id}")
                Log.d("MyAppsRepositoryImpl", "Icono a subir: ${iconPart != null}")
                Log.d("MyAppsRepositoryImpl", "APK a subir: ${apkPart != null}")
                Log.d("MyAppsRepositoryImpl", "Nuevas capturas a subir: ${screenshotParts.size}")
                Log.d("MyAppsRepositoryImpl", "URLs de capturas a mantener: ${screenshotsToKeepUrls.size}")


                val uploadFilesResponse = myAppsService.uploadAppFiles(
                    appId = appIdToUpdate.toRequestBody("text/plain".toMediaType()),
                    icon = iconPart,
                    appFile = apkPart,
                    screenshots = screenshotParts.ifEmpty { null }, // Si está vacío, se envía null
                    screenshotsToKeep = screenshotsToKeepPart
                )

                if (!uploadFilesResponse.isSuccessful) {
                    val errorBody = uploadFilesResponse.errorBody()?.string()
                    return Result.failure(
                        Exception("Error al subir archivos de la app: ${uploadFilesResponse.code()} - ${errorBody ?: uploadFilesResponse.message()}")
                    )
                }
                Log.d("MyAppsRepositoryImpl", "Archivos actualizados subidos con éxito.")
            } else {
                Log.d("MyAppsRepositoryImpl", "No hay archivos nuevos para subir y la lista de capturas a mantener ya se manejó. Solo se actualizaron los metadatos.")
            }

            Result.success(updatedApp)

        } catch (e: HttpException) {
            val errorMessage = when (e.code()) {
                400 -> "Datos inválidos en la solicitud de actualización."
                404 -> "Aplicación no encontrada para actualizar."
                413 -> "Archivo demasiado grande."
                else -> "Error del servidor: ${e.code()} - ${e.message()}"
            }
            Log.e("MyAppsRepositoryImpl", "HttpException al actualizar app: $errorMessage", e)
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("MyAppsRepositoryImpl", "Excepción inesperada al actualizar app: ${e.message}", e)
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
                screenshots = screenshotParts.ifEmpty { null },
                screenshotsToKeep = null
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
