package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.KmLog
import com.moto.tracker.domain.model.MaintenanceLog
import com.moto.tracker.domain.model.MaintenanceTask
import com.moto.tracker.domain.model.NextDueResult
import kotlinx.coroutines.flow.Flow

interface MaintenanceRepository {
    // Tasks
    fun observeTasksByVehicle(vehicleId: Long): Flow<List<MaintenanceTask>>
    fun observeTaskById(id: Long): Flow<MaintenanceTask?>
    suspend fun getTaskById(id: Long): MaintenanceTask?
    suspend fun getActiveTasksByVehicle(vehicleId: Long): List<MaintenanceTask>
    suspend fun getAllActiveTasks(): List<MaintenanceTask>
    suspend fun getOverdueTasks(nowMillis: Long): List<MaintenanceTask>
    suspend fun getTasksDueSoon(nowMillis: Long, soonMillis: Long, kmBuffer: Int): List<MaintenanceTask>
    suspend fun insertTask(task: MaintenanceTask): Long
    suspend fun updateTask(task: MaintenanceTask)
    suspend fun updateTaskAfterLog(id: Long, date: Long, odometer: Int, nextDueResult: NextDueResult)
    suspend fun deactivateTask(id: Long)

    // Logs
    fun observeLogsByTask(taskId: Long): Flow<List<MaintenanceLog>>
    fun observeLogsByVehicle(vehicleId: Long): Flow<List<MaintenanceLog>>
    suspend fun getLogsByVehicleAndDateRange(vehicleId: Long, from: Long, to: Long): List<MaintenanceLog>
    suspend fun insertLog(log: MaintenanceLog): Long
    suspend fun deleteLog(log: MaintenanceLog)

    // Km Logs
    fun observeKmLogsByVehicle(vehicleId: Long): Flow<List<KmLog>>
    suspend fun getKmLogByVehicleAndDate(vehicleId: Long, date: Long): KmLog?
    suspend fun getLatestKmLog(vehicleId: Long): KmLog?
    suspend fun getTotalKmInRange(vehicleId: Long, from: Long, to: Long): Int
    suspend fun insertKmLog(log: KmLog): Long
}
