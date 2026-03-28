package com.moto.tracker.data.repository

import android.content.Context
import androidx.work.*
import com.moto.tracker.data.worker.AppointmentReminderWorker
import com.moto.tracker.data.worker.DailyKmReminderWorker
import com.moto.tracker.data.worker.MaintenanceCheckWorker
import com.moto.tracker.domain.model.ServiceAppointment
import com.moto.tracker.domain.repository.NotificationScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NotificationSchedulerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val workManager: WorkManager
) : NotificationScheduler {

    override fun scheduleDailyKmReminder(hourOfDay: Int, minute: Int) {
        val delay = computeDelayToNextTrigger(hourOfDay, minute)
        val request = PeriodicWorkRequestBuilder<DailyKmReminderWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(TAG_KM_REMINDER)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_KM_REMINDER,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    override fun cancelDailyKmReminder() {
        workManager.cancelUniqueWork(WORK_KM_REMINDER)
    }

    override fun scheduleMaintenanceCheck() {
        val request = PeriodicWorkRequestBuilder<MaintenanceCheckWorker>(12, TimeUnit.HOURS)
            .setInitialDelay(1, TimeUnit.HOURS)
            .addTag(TAG_MAINTENANCE_CHECK)
            .build()
        workManager.enqueueUniquePeriodicWork(
            WORK_MAINTENANCE_CHECK,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    override fun scheduleAppointmentReminder(appointment: ServiceAppointment) {
        val now = System.currentTimeMillis()
        val triggerAt = appointment.appointmentDate - appointment.reminderMinutesBefore * 60 * 1000L
        val delay = triggerAt - now
        if (delay <= 0) return

        val data = workDataOf(AppointmentReminderWorker.KEY_APPOINTMENT_ID to appointment.id)
        val request = OneTimeWorkRequestBuilder<AppointmentReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .addTag(TAG_APPOINTMENT_REMINDER)
            .build()
        workManager.enqueueUniqueWork(
            "${WORK_APPOINTMENT_PREFIX}${appointment.id}",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    override fun cancelAppointmentReminder(appointmentId: Long) {
        workManager.cancelUniqueWork("${WORK_APPOINTMENT_PREFIX}${appointmentId}")
    }

    override fun rescheduleAll(hourOfDay: Int, minute: Int) {
        scheduleDailyKmReminder(hourOfDay, minute)
        scheduleMaintenanceCheck()
    }

    private fun computeDelayToNextTrigger(hourOfDay: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hourOfDay)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    companion object {
        const val WORK_KM_REMINDER = "daily_km_reminder"
        const val WORK_MAINTENANCE_CHECK = "maintenance_check"
        const val WORK_APPOINTMENT_PREFIX = "appointment_reminder_"
        const val TAG_KM_REMINDER = "km_reminder"
        const val TAG_MAINTENANCE_CHECK = "maintenance_check"
        const val TAG_APPOINTMENT_REMINDER = "appointment_reminder"
    }
}
