package com.moto.tracker.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.moto.tracker.data.local.dao.KmLogDao
import com.moto.tracker.data.local.dao.VehicleDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.data.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

@HiltWorker
class DailyKmReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val vehicleDao: VehicleDao,
    private val kmLogDao: KmLogDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val today = startOfDay(System.currentTimeMillis())
        val vehicles = vehicleDao.getAll()

        vehicles.forEachIndexed { index, vehicleEntity ->
            val alreadyLogged = kmLogDao.getByVehicleAndDate(vehicleEntity.id, today)
            if (alreadyLogged == null) {
                NotificationHelper.showKmReminderNotification(
                    context = applicationContext,
                    vehicleNickname = vehicleEntity.nickname.ifBlank { "${vehicleEntity.year} ${vehicleEntity.model}" },
                    notificationId = 1000 + index
                )
            }
        }
        return Result.success()
    }

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
