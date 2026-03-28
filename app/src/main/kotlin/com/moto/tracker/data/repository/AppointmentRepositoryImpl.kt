package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.ServiceAppointmentDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.data.local.mapper.toEntity
import com.moto.tracker.domain.model.AppointmentStatus
import com.moto.tracker.domain.model.ServiceAppointment
import com.moto.tracker.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(
    private val dao: ServiceAppointmentDao
) : AppointmentRepository {

    override fun observeByVehicle(vehicleId: Long): Flow<List<ServiceAppointment>> =
        dao.observeByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): ServiceAppointment? = dao.getById(id)?.toDomain()

    override suspend fun getUpcomingAppointments(now: Long): List<ServiceAppointment> =
        dao.getUpcomingAppointments(now).map { it.toDomain() }

    override suspend fun getAppointmentsWithReminders(now: Long): List<ServiceAppointment> =
        dao.getAppointmentsWithReminders(now).map { it.toDomain() }

    override suspend fun insert(appointment: ServiceAppointment): Long =
        dao.insert(appointment.toEntity())

    override suspend fun update(appointment: ServiceAppointment) =
        dao.update(appointment.toEntity())

    override suspend fun delete(appointment: ServiceAppointment) =
        dao.delete(appointment.toEntity())

    override suspend fun updateStatus(id: Long, status: AppointmentStatus) =
        dao.updateStatus(id, status.name.lowercase(), System.currentTimeMillis())
}
