package com.moto.tracker.data.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.moto.tracker.MaintenanceTrackerApp
import com.moto.tracker.data.local.dao.VehicleDao
import com.moto.tracker.domain.repository.RecallRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class RecallCheckWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val vehicleDao: VehicleDao,
    private val recallRepository: RecallRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val vehicles = vehicleDao.getAll()
        var notifId = 5000

        vehicles.forEach { vehicle ->
            val fetchResult = recallRepository.fetchAndStoreRecalls(
                vehicleId = vehicle.id,
                make = vehicle.make,
                model = vehicle.model,
                year = vehicle.year
            )

            val newRecallCount = fetchResult.getOrNull() ?: 0
            if (newRecallCount > 0) {
                showRecallNotification(
                    vehicleName = vehicle.nickname.ifBlank { "${vehicle.year} ${vehicle.make} ${vehicle.model}" },
                    recallCount = newRecallCount,
                    notificationId = notifId++
                )
            }
        }

        return Result.success()
    }

    private fun showRecallNotification(vehicleName: String, recallCount: Int, notificationId: Int) {
        val title = "Safety Recall Alert"
        val message = "$recallCount new recall${if (recallCount > 1) "s" else ""} found for $vehicleName"

        val notification = NotificationCompat.Builder(applicationContext, MaintenanceTrackerApp.CHANNEL_RECALL_ALERT)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Notification permission not granted
        }
    }
}
