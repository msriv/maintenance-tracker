package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.AppointmentStatus
import com.moto.tracker.domain.model.ServiceAppointment
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    fun observeByVehicle(vehicleId: Long): Flow<List<ServiceAppointment>>
    suspend fun getById(id: Long): ServiceAppointment?
    suspend fun getUpcomingAppointments(now: Long): List<ServiceAppointment>
    suspend fun getAppointmentsWithReminders(now: Long): List<ServiceAppointment>
    suspend fun insert(appointment: ServiceAppointment): Long
    suspend fun update(appointment: ServiceAppointment)
    suspend fun delete(appointment: ServiceAppointment)
    suspend fun updateStatus(id: Long, status: AppointmentStatus)
}
