package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.MaintenanceLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceLogDao {
    @Query("SELECT * FROM maintenance_logs WHERE taskId = :taskId ORDER BY performedDate DESC")
    fun observeByTask(taskId: Long): Flow<List<MaintenanceLogEntity>>

    @Query("SELECT * FROM maintenance_logs WHERE vehicleId = :vehicleId ORDER BY performedDate DESC")
    fun observeByVehicle(vehicleId: Long): Flow<List<MaintenanceLogEntity>>

    @Query("SELECT * FROM maintenance_logs WHERE vehicleId = :vehicleId AND performedDate BETWEEN :from AND :to ORDER BY performedDate DESC")
    suspend fun getByVehicleAndDateRange(vehicleId: Long, from: Long, to: Long): List<MaintenanceLogEntity>

    @Query("SELECT * FROM maintenance_logs WHERE taskId = :taskId ORDER BY performedDate DESC LIMIT 1")
    suspend fun getLatestForTask(taskId: Long): MaintenanceLogEntity?

    @Insert
    suspend fun insert(log: MaintenanceLogEntity): Long

    @Delete
    suspend fun delete(log: MaintenanceLogEntity)
}
