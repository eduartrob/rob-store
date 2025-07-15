package com.robstore.core.hardware.location.domain.useCase

import com.robstore.core.hardware.location.domain.repository.LocationRepository

class LocationUseCase(private val locationRepository: LocationRepository) {
    suspend operator fun invoke(): Result<String> {
        val locationResult = locationRepository.getLastLocation()
        return if (locationResult.isSuccess) {
            val location = locationResult.getOrThrow()
            locationRepository.getRegionFromLocation(location)
        } else {
            Result.failure(locationResult.exceptionOrNull() ?: Exception("No se pudo obtener la ubicación."))
        }
    }
}