package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.KmLogDao
import com.moto.tracker.data.local.dao.MaintenanceLogDao
import com.moto.tracker.data.local.dao.MaintenanceTaskDao
import com.moto.tracker.data.local.mapper.*
import com.moto.tracker.domain.model.KmLog
import com.moto.tracker.domain.model.MaintenanceLog
import com.moto.tracker.domain.model.MaintenanceTask
import com.moto.tracker.domain.model.NextDueResult
import com.moto.tracker.domain.repository.MaintenanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MaintenanceRepositoryImpl @Inject constructor(
    private val taskDao: MaintenanceTaskDao,
    private val logDao: MaintenanceLogDao,
    private val kmLogDao: KmLogDao
) : MaintenanceRepository {

    override fun observeTasksByVehicle(vehicleId: Long): Flow<List<MaintenanceTask>> =
        taskDao.observeByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override fun observeTaskById(id: Long): Flow<MaintenanceTask?> =
        taskDao.observeById(id).map { it?.toDomain() }

    override suspend fun getTaskById(id: Long): MaintenanceTask? = taskDao.getById(id)?.toDomain()

    override suspend fun getActiveTasksByVehicle(vehicleId: Long): List<MaintenanceTask> =
        taskDao.getActiveByVehicle(vehicleId).map { it.toDomain() }

    override suspend fun getAllActiveTasks(): List<MaintenanceTask> =
        taskDao.getAllActive().map { it.toDomain() }

    override suspend fun getOverdueTasks(nowMillis: Long): List<MaintenanceTask> =
        taskDao.getOverdueTasks(nowMillis).map { it.toDomain() }

    override suspend fun getTasksDueSoon(nowMillis: Long, soonMillis: Long, kmBuffer: Int): List<MaintenanceTask> =
        taskDao.getTasksDueSoon(nowMillis, soonMillis, kmBuffer).map { it.toDomain() }

    override suspend fun insertTask(task: MaintenanceTask): Long = taskDao.insert(task.toEntity())

    override suspend fun updateTask(task: MaintenanceTask) = taskDao.update(task.toEntity())

    override suspend fun updateTaskAfterLog(id: Long, date: Long, odometer: Int, nextDueResult: NextDueResult) =
        taskDao.updateAfterLog(
            id = id,
            date = date,
            odometer = odometer,
            nextDate = nextDueResult.nextDueDate,
            nextOdometer = nextDueResult.nextDueOdometer,
            updatedAt = System.currentTimeMillis()
        )

    override suspend fun deactivateTask(id: Long) = taskDao.deactivate(id, System.currentTimeMillis())

    override fun observeLogsByTask(taskId: Long): Flow<List<MaintenanceLog>> =
        logDao.observeByTask(taskId).map { list -> list.map { it.toDomain() } }

    override fun observeLogsByVehicle(vehicleId: Long): Flow<List<MaintenanceLog>> =
        logDao.observeByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override suspend fun getLogsByVehicleAndDateRange(vehicleId: Long, from: Long, to: Long): List<MaintenanceLog> =
        logDao.getByVehicleAndDateRange(vehicleId, from, to).map { it.toDomain() }

    override suspend fun insertLog(log: MaintenanceLog): Long = logDao.insert(log.toEntity())

    override suspend fun deleteLog(log: MaintenanceLog) = logDao.delete(log.toEntity())

    override fun observeKmLogsByVehicle(vehicleId: Long): Flow<List<KmLog>> =
        kmLogDao.observeByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override suspend fun getKmLogByVehicleAndDate(vehicleId: Long, date: Long): KmLog? =
        kmLogDao.getByVehicleAndDate(vehicleId, date)?.toDomain()

    override suspend fun getLatestKmLog(vehicleId: Long): KmLog? =
        kmLogDao.getLatestByVehicle(vehicleId)?.toDomain()

    override suspend fun getTotalKmInRange(vehicleId: Long, from: Long, to: Long): Int =
        kmLogDao.getTotalKmInRange(vehicleId, from, to) ?: 0

    override suspend fun insertKmLog(log: KmLog): Long = kmLogDao.insert(log.toEntity())
}
