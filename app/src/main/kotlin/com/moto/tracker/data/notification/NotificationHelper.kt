package com.moto.tracker.data.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.moto.tracker.MainActivity
import com.moto.tracker.MaintenanceTrackerApp
import com.moto.tracker.R

object NotificationHelper {

    fun showKmReminderNotification(context: Context, vehicleNickname: String, notificationId: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, MaintenanceTrackerApp.CHANNEL_KM_REMINDER)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("Log today's km — $vehicleNickname")
            .setContentText("Don't forget to record your odometer reading for today.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun showMaintenanceDueNotification(
        context: Context,
        vehicleNickname: String,
        taskName: String,
        isOverdue: Boolean,
        notificationId: Int
    ) {
        val title = if (isOverdue) "Overdue: $taskName" else "Due soon: $taskName"
        val body = if (isOverdue) "$taskName for $vehicleNickname is overdue. Schedule service now."
                   else "$taskName for $vehicleNickname is due soon."

        val notification = NotificationCompat.Builder(context, MaintenanceTrackerApp.CHANNEL_MAINTENANCE_DUE)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    fun showAppointmentReminderNotification(
        context: Context,
        appointmentTitle: String,
        vehicleNickname: String,
        serviceCenterName: String?,
        notificationId: Int
    ) {
        val body = buildString {
            append("$appointmentTitle for $vehicleNickname")
            if (serviceCenterName != null) append(" at $serviceCenterName")
        }

        val notification = NotificationCompat.Builder(context, MaintenanceTrackerApp.CHANNEL_APPOINTMENT_REMINDER)
            .setSmallIcon(android.R.drawable.ic_menu_agenda)
            .setContentTitle("Upcoming: $appointmentTitle")
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }
}
