package com.robstore.core.common.notifications

interface INotificationService {
    fun showSuccess(message: String)
    fun showError(message: String, title: String = "Error")
    fun showWarning(message: String)
    fun showInfo(message: String)
    fun showInfoDeleteApps(message: String)
}