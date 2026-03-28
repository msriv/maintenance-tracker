package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.ServiceAppointmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceAppointmentDao {
    @Query("SELECT * FROM service_appointments WHERE vehicleId = :vehicleId ORDER BY appointmentDate ASC")
    fun observeByVehicle(vehicleId: Long): Flow<List<ServiceAppointmentEntity>>

    @Query("SELECT * FROM service_appointments WHERE id = :id")
    suspend fun getById(id: Long): ServiceAppointmentEntity?

    @Query("SELECT * FROM service_appointments WHERE appointmentDate >= :now AND status = 'upcoming' ORDER BY appointmentDate ASC")
    suspend fun getUpcomingAppointments(now: Long): List<ServiceAppointmentEntity>

    @Query("SELECT * FROM service_appointments WHERE reminderEnabled = 1 AND status = 'upcoming' AND appointmentDate > :now")
    suspend fun getAppointmentsWithReminders(now: Long): List<ServiceAppointmentEntity>

    @Insert
    suspend fun insert(appointment: ServiceAppointmentEntity): Long

    @Update
    suspend fun update(appointment: ServiceAppointmentEntity)

    @Delete
    suspend fun delete(appointment: ServiceAppointmentEntity)

    @Query("UPDATE service_appointments SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, updatedAt: Long)
}
