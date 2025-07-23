package com.robstore.app.ui.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.robstore.MainActivity
import com.robstore.R
import com.robstore.core.common.notifications.INotificationService

class AndroidNotificationService(private val context: Context) : INotificationService {
    companion object {
        private const val CHANNEL_ID = "robstore_general_channel"
        private const val CHANNEL_NAME = "Notificaciones de RobStore"
        private const val NOTIFICATION_ID_SUCCESS = 1001
        private const val NOTIFICATION_ID_ERROR = 1002
        private const val NOTIFICATION_ID_WARNING = 1003
        private const val NOTIFICATION_ID_INFO = 1004
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones generales para la aplicación RobStore"
            enableLights(true)
            enableVibration(true)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun showSuccess(message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0, // Request code
            intent,
            PendingIntent.FLAG_IMMUTABLE // Obligatorio a partir de Android S (API 31)
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logoprueba) // <-- ¡Necesitas un icono pequeño aquí!
            // Usualmente es `ic_notification` o `ic_launcher_foreground`
            .setContentTitle("RobStore - Éxito") // Título de la notificación
            .setContentText(message) // Contenido del mensaje
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta para que sea heads-up si es posible
            .setContentIntent(pendingIntent) // Qué hacer al tocar la notificación
            .setAutoCancel(true) // La notificación se quita automáticamente al tocarla

        notificationManager.notify(NOTIFICATION_ID_SUCCESS, builder.build())
    }

    override fun showError(message: String, title: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logoprueba)
            .setContentTitle("RobStore - $title")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Errores suelen ser de alta prioridad
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setColor(context.resources.getColor(R.color.purple_700, null))
        notificationManager.notify(NOTIFICATION_ID_ERROR, builder.build())
    }

    override fun showWarning(message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("RobStore - Advertencia")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioridad normal
            .setAutoCancel(true)
        notificationManager.notify(NOTIFICATION_ID_WARNING, builder.build())
    }

    override fun showInfo(message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("RobStore - Información")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setAutoCancel(true)
        notificationManager.notify(NOTIFICATION_ID_INFO, builder.build())
    }
}