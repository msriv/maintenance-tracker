package com.moto.tracker

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.moto.tracker.data.local.ManufacturerScheduleSeeder
import com.moto.tracker.data.local.SeasonalChecklistSeeder
import com.moto.tracker.data.worker.DocumentExpiryWorker
import com.moto.tracker.data.worker.RecallCheckWorker
import com.moto.tracker.data.worker.RegistrySyncWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MaintenanceTrackerApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var manufacturerScheduleSeeder: ManufacturerScheduleSeeder

    @Inject
    lateinit var seasonalChecklistSeeder: SeasonalChecklistSeeder

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        appScope.launch { manufacturerScheduleSeeder.seedIfEmpty() }
        appScope.launch { seasonalChecklistSeeder.seedIfEmpty() }
        schedulePeriodicWorkers()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    private fun schedulePeriodicWorkers() {
        val workManager = WorkManager.getInstance(this)

        // Registry sync - daily with network constraint
        val registrySyncRequest = PeriodicWorkRequestBuilder<RegistrySyncWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        workManager.enqueueUniquePeriodicWork(
            "registry_sync",
            ExistingPeriodicWorkPolicy.KEEP,
            registrySyncRequest
        )

        // Recall check - weekly
        val recallCheckRequest = PeriodicWorkRequestBuilder<RecallCheckWorker>(7, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        workManager.enqueueUniquePeriodicWork(
            "recall_check",
            ExistingPeriodicWorkPolicy.KEEP,
            recallCheckRequest
        )

        // Document expiry check - daily
        val docExpiryRequest = PeriodicWorkRequestBuilder<DocumentExpiryWorker>(1, TimeUnit.DAYS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "document_expiry_check",
            ExistingPeriodicWorkPolicy.KEEP,
            docExpiryRequest
        )
    }

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
            },
            NotificationChannel(
                CHANNEL_RECALL_ALERT,
                "Recall Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when a safety recall affects your vehicle"
                enableVibration(true)
            }
        )

        channels.forEach { notificationManager.createNotificationChannel(it) }
    }

    companion object {
        const val CHANNEL_KM_REMINDER = "km_reminder"
        const val CHANNEL_MAINTENANCE_DUE = "maintenance_due"
        const val CHANNEL_APPOINTMENT_REMINDER = "appointment_reminder"
        const val CHANNEL_DOCUMENT_EXPIRY = "document_expiry"
        const val CHANNEL_RECALL_ALERT = "recall_alert"
    }
}
