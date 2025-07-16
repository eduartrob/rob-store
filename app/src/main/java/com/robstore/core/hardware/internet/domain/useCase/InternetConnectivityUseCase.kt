package com.robstore.core.hardware.internet.domain.useCase

import com.robstore.core.hardware.internet.domain.repository.InternetConnectivityRepository
import kotlinx.coroutines.flow.Flow

class InternetConnectivityUseCase(
    private val internetConnectivityRepository: InternetConnectivityRepository
) {
    fun observeConnectivity(): Flow<Boolean> {
        return internetConnectivityRepository.connectivityStatus()
    }
}