package com.robstore.features.myApps.domain.useCase

import com.robstore.core.common.notifications.INotificationService

class MyAppsNotificationsUseCase(
    private val notificationService: INotificationService
) {
    fun showAppAddedOrUpdatedSuccess(appName: String) {
        notificationService.showSuccess("La aplicación '$appName' se ha guardado correctamente.")
    }
    fun showAppAddSuccess(appName: String){
        notificationService.showSuccess("La aplicación '$appName' se ha creado correctamente.")
    }
    fun showAppDeleteSuccess(appName: String){
        notificationService.showInfoDeleteApps("La aplicación '$appName' se ha borrado.")
    }

    fun saveLocalData(message: String){
        notificationService.showInfo(message)
    }
    fun successUpdataData(message: String){
        notificationService.showSuccess(message)
    }
    fun errorUpdateData(message: String){
        notificationService.showError(title = "Error de sincronización", message = message)
    }
    fun infoData(message: String) {
        notificationService.showInfo(message)
    }


}