package com.moto.tracker.data.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.moto.tracker.MaintenanceTrackerApp
import com.moto.tracker.data.local.dao.DocumentDao
import com.moto.tracker.data.local.dao.VehicleDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class DocumentExpiryWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val documentDao: DocumentDao,
    private val vehicleDao: VehicleDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        val thirtyDaysFromNow = now + TimeUnit.DAYS.toMillis(30)

        val expiringDocuments = documentDao.getDocumentsExpiringBetween(now, thirtyDaysFromNow)
        var notifId = 4000

        expiringDocuments.forEach { document ->
            val vehicle = vehicleDao.getById(document.vehicleId)
            val vehicleName = vehicle?.let {
                it.nickname.ifBlank { "${it.year} ${it.make} ${it.model}" }
            } ?: "Unknown Vehicle"

            val expiryDate = document.expiryDate ?: return@forEach
            val daysUntilExpiry = TimeUnit.MILLISECONDS.toDays(expiryDate - now).toInt()
            val message = when {
                daysUntilExpiry <= 0 -> "${document.title} for $vehicleName has expired"
                daysUntilExpiry == 1 -> "${document.title} for $vehicleName expires tomorrow"
                else -> "${document.title} for $vehicleName expires in $daysUntilExpiry days"
            }

            showDocumentExpiryNotification(message, notifId++)
        }

        return Result.success()
    }

    private fun showDocumentExpiryNotification(message: String, notificationId: Int) {
        val notification = NotificationCompat.Builder(applicationContext, MaintenanceTrackerApp.CHANNEL_DOCUMENT_EXPIRY)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Document Expiry Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(applicationContext).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Notification permission not granted
        }
    }
}
