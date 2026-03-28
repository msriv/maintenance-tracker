package com.moto.tracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.moto.tracker.data.local.ManufacturerScheduleSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MaintenanceTrackerApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var manufacturerScheduleSeeder: ManufacturerScheduleSeeder

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        appScope.launch { manufacturerScheduleSeeder.seedIfEmpty() }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val notificationManager = getSystemService(NotificationManager::class.java)

        val channels = listOf(
            NotificationChannel(
                CHANNEL_KM_REMINDER,
                "Daily Km Reminder",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to log your daily km reading"
                enableVibration(false)
            },
            NotificationChannel(
                CHANNEL_MAINTENANCE_DUE,
                "Maintenance Due",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when vehicle maintenance is due or overdue"
                enableVibration(true)
            },
            NotificationChannel(
                CHANNEL_APPOINTMENT_REMINDER,
                "Appointment Reminder",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminds you of upcoming service appointments"
                enableVibration(true)
            },
            NotificationChannel(
                CHANNEL_DOCUMENT_EXPIRY,
                "Document Expiry",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Alerts when vehicle documents are about to expire"
                enableVibration(false)
            }
        )

        channels.forEach { notificationManager.createNotificationChannel(it) }
    }

    companion object {
        const val CHANNEL_KM_REMINDER = "km_reminder"
        const val CHANNEL_MAINTENANCE_DUE = "maintenance_due"
        const val CHANNEL_APPOINTMENT_REMINDER = "appointment_reminder"
        const val CHANNEL_DOCUMENT_EXPIRY = "document_expiry"
    }
}
