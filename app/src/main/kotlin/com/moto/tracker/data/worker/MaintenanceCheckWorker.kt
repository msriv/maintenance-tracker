package com.moto.tracker.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.moto.tracker.data.local.dao.MaintenanceTaskDao
import com.moto.tracker.data.local.dao.VehicleDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.data.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MaintenanceCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val maintenanceTaskDao: MaintenanceTaskDao,
    private val vehicleDao: VehicleDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val now = System.currentTimeMillis()
        val sevenDaysMs = 7L * 24 * 60 * 60 * 1000

        val overdueTasks = maintenanceTaskDao.getOverdueTasks(now)
        val dueSoonTasks = maintenanceTaskDao.getTasksDueSoon(now, now + sevenDaysMs, kmBuffer = 500)

        // Group by vehicle and notify
        val notifiedVehicles = mutableSetOf<Long>()
        var notifId = 2000

        (overdueTasks + dueSoonTasks)
            .distinctBy { it.id }
            .groupBy { it.vehicleId }
            .forEach { (vehicleId, tasks) ->
                val vehicle = vehicleDao.getById(vehicleId) ?: return@forEach
                val vehicleName = vehicle.nickname.ifBlank { "${vehicle.year} ${vehicle.model}" }

                val overdueCount = tasks.count { task ->
                    overdueTasks.any { it.id == task.id }
                }

                tasks.forEach { task ->
                    val isOverdue = overdueTasks.any { it.id == task.id }
                    NotificationHelper.showMaintenanceDueNotification(
                        context = applicationContext,
                        vehicleNickname = vehicleName,
                        taskName = task.customName ?: task.taskType,
                        isOverdue = isOverdue,
                        notificationId = notifId++
                    )
                }
            }

        return Result.success()
    }
}
