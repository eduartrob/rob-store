package com.robstore.features.myApps.di

import android.content.Context
import com.robstore.core.network.RetrofitHelper
import com.robstore.features.myApps.data.datasource.MyAppsService
import com.robstore.features.myApps.data.repository.MyAppsRepositoryImpl
import com.robstore.features.myApps.domain.repository.MyAppsRepository
import com.robstore.features.myApps.domain.useCase.MyAppsUseCase
import com.robstore.features.myApps.presentation.viewModel.MyAppsViewModel

object MyAppsModule {
    // Instancia de MyAppsService
    private val myAppsService: MyAppsService by lazy {
        RetrofitHelper.getMyAppsService()
    }


    // Instancia de MyAppsRepositoryImpl
    private val myAppsRepositoryImpl: MyAppsRepositoryImpl by lazy {
        MyAppsRepositoryImpl(myAppsService) // Pasa ambas dependencias
    }

    // Exposición de la interfaz MyAppsRepository
    val myAppsRepository: MyAppsRepository by lazy {
        myAppsRepositoryImpl
    }

    // Instancia de MyAppsUseCase
    val myAppsUseCase: MyAppsUseCase by lazy {
        MyAppsUseCase(myAppsRepository)
    }

    object MyAppsModuleProvider {
        fun getMyAppsViewModel(applicationContext: Context): MyAppsViewModel {
            return MyAppsViewModel(
                myAppsUseCase,
                applicationContext = applicationContext // Pasa el contexto recibido
            )
        }
    }
}