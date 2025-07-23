package com.robstore.features.myApps.presentation.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.robstore.core.common.notifications.INotificationService
import com.robstore.features.myApps.domain.useCase.MyAppsNotificationsUseCase
import com.robstore.features.myApps.domain.useCase.MyAppsUseCase

class MyAppsViewModelFactory(
    private val myAppsUseCase: MyAppsUseCase,
    private val applicationContext: Context,
    private val myAppsNotificationsUseCase: MyAppsNotificationsUseCase,
    ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyAppsViewModel::class.java)) {
            return MyAppsViewModel(
                myAppsUseCase = myAppsUseCase,
                applicationContext = applicationContext,
                myAppsNotificationsUseCase = myAppsNotificationsUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
