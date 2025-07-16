package com.robstore.features.home.di

import com.robstore.core.network.RetrofitHelper
import com.robstore.features.home.data.datasource.HomeService
import com.robstore.features.home.data.repository.HomeRepositoryImpl
import com.robstore.features.home.domain.repository.HomeRepository
import com.robstore.features.home.domain.useCase.HomeUseCase

object HomeAppModul {
    private val homeService: HomeService by lazy {
        RetrofitHelper.getHomeService()
    }

    val homeRepositoryImpl: HomeRepositoryImpl by lazy {
        HomeRepositoryImpl(homeService)
    }

    val homeRepository: HomeRepository by lazy {
        homeRepositoryImpl
    }

    val homeUseCase: HomeUseCase by lazy {
        HomeUseCase(homeRepository)
    }

    object HomeAppModule {
        fun getHomeUseCase(): HomeUseCase {
            return HomeUseCase(
                homeRepository
            )
        }
    }
}