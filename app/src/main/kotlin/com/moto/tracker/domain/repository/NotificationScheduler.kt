package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.ServiceAppointment

interface NotificationScheduler {
    fun scheduleDailyKmReminder(hourOfDay: Int, minute: Int)
    fun cancelDailyKmReminder()
    fun scheduleMaintenanceCheck()
    fun scheduleAppointmentReminder(appointment: ServiceAppointment)
    fun cancelAppointmentReminder(appointmentId: Long)
    fun rescheduleAll(hourOfDay: Int, minute: Int)
}
