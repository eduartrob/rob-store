package com.robstore.features.myApps.di

import android.content.Context
import com.robstore.core.common.notifications.INotificationService
import com.robstore.core.network.RetrofitHelper
import com.robstore.features.authentication.login.di.AppModule
import com.robstore.features.myApps.data.datasource.MyAppsService
import com.robstore.features.myApps.data.repository.MyAppsRepositoryImpl
import com.robstore.features.myApps.domain.repository.MyAppsRepository
import com.robstore.features.myApps.domain.useCase.MyAppsNotificationsUseCase
import com.robstore.features.myApps.domain.useCase.MyAppsUseCase
import com.robstore.features.myApps.presentation.viewModel.MyAppsViewModel

object MyAppsModule {
    private val myAppsService: MyAppsService by lazy {
        RetrofitHelper.getMyAppsService()
    }

    private val myAppsRepositoryImpl: MyAppsRepositoryImpl by lazy {
        MyAppsRepositoryImpl(myAppsService)
    }

    // Exposición de la interfaz MyAppsRepository
    private val myAppsRepository: MyAppsRepository by lazy {
        myAppsRepositoryImpl
    }

    val myAppsUseCase: MyAppsUseCase by lazy {
        MyAppsUseCase(myAppsRepository)
    }

    private val notificationService: INotificationService by lazy {
        AppModule.getNotificationService()
    }

    val myAppsNotificationsUseCase: MyAppsNotificationsUseCase by lazy {
        MyAppsNotificationsUseCase(notificationService)
    }
}