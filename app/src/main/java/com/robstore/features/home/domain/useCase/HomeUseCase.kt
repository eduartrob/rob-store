package com.robstore.features.home.domain.useCase

import com.robstore.features.home.domain.model.Picture
import com.robstore.features.home.domain.repository.HomeRepository
import com.robstore.features.profile.domain.model.UpdateUser

class HomeUseCase(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(): Result<Picture>{
        return homeRepository.getImageProfile()
    }
}