package com.moto.tracker.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.moto.tracker.data.local.dao.ServiceAppointmentDao
import com.moto.tracker.data.local.dao.VehicleDao
import com.moto.tracker.data.notification.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AppointmentReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val appointmentDao: ServiceAppointmentDao,
    private val vehicleDao: VehicleDao
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val appointmentId = inputData.getLong(KEY_APPOINTMENT_ID, -1L)
        if (appointmentId == -1L) return Result.failure()

        val appointment = appointmentDao.getById(appointmentId) ?: return Result.success()
        if (appointment.status != "upcoming") return Result.success()

        val vehicle = vehicleDao.getById(appointment.vehicleId)
        val vehicleName = vehicle?.let { it.nickname.ifBlank { "${it.year} ${it.model}" } } ?: "Vehicle"

        NotificationHelper.showAppointmentReminderNotification(
            context = applicationContext,
            appointmentTitle = appointment.title,
            vehicleNickname = vehicleName,
            serviceCenterName = appointment.serviceCenterName,
            notificationId = (3000 + appointmentId).toInt()
        )

        return Result.success()
    }

    companion object {
        const val KEY_APPOINTMENT_ID = "appointment_id"
    }
}
