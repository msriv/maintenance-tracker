package com.moto.tracker.domain.usecase.appointment

import com.moto.tracker.domain.model.ServiceAppointment
import com.moto.tracker.domain.repository.AppointmentRepository
import com.moto.tracker.domain.repository.NotificationScheduler
import javax.inject.Inject

class AddAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val notificationScheduler: NotificationScheduler
) {
    suspend operator fun invoke(appointment: ServiceAppointment): Result<Long> = runCatching {
        val id = repository.insert(appointment)
        val savedAppointment = appointment.copy(id = id)
        if (appointment.reminderEnabled) {
            notificationScheduler.scheduleAppointmentReminder(savedAppointment)
        }
        id
    }
}
