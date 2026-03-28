package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.MaintenanceTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceTaskDao {
    @Query("SELECT * FROM maintenance_tasks WHERE vehicleId = :vehicleId AND isActive = 1 ORDER BY nextDueDate ASC NULLS LAST")
    fun observeByVehicle(vehicleId: Long): Flow<List<MaintenanceTaskEntity>>

    @Query("SELECT * FROM maintenance_tasks WHERE id = :id")
    fun observeById(id: Long): Flow<MaintenanceTaskEntity?>

    @Query("SELECT * FROM maintenance_tasks WHERE id = :id")
    suspend fun getById(id: Long): MaintenanceTaskEntity?

    @Query("SELECT * FROM maintenance_tasks WHERE vehicleId = :vehicleId AND isActive = 1")
    suspend fun getActiveByVehicle(vehicleId: Long): List<MaintenanceTaskEntity>

    @Query("SELECT * FROM maintenance_tasks WHERE isActive = 1")
    suspend fun getAllActive(): List<MaintenanceTaskEntity>

    @Query("""
        SELECT mt.* FROM maintenance_tasks mt
        JOIN vehicles v ON mt.vehicleId = v.id
        WHERE mt.isActive = 1
        AND (
            (mt.nextDueDate IS NOT NULL AND mt.nextDueDate < :nowMillis)
            OR
            (mt.nextDueOdometer IS NOT NULL AND mt.nextDueOdometer <= v.currentOdometer)
        )
    """)
    suspend fun getOverdueTasks(nowMillis: Long): List<MaintenanceTaskEntity>

    @Query("""
        SELECT mt.* FROM maintenance_tasks mt
        JOIN vehicles v ON mt.vehicleId = v.id
        WHERE mt.isActive = 1
        AND (
            (mt.nextDueDate IS NOT NULL AND mt.nextDueDate BETWEEN :nowMillis AND :soonMillis)
            OR
            (mt.nextDueOdometer IS NOT NULL AND mt.nextDueOdometer BETWEEN v.currentOdometer AND v.currentOdometer + :kmBuffer)
        )
    """)
    suspend fun getTasksDueSoon(nowMillis: Long, soonMillis: Long, kmBuffer: Int): List<MaintenanceTaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: MaintenanceTaskEntity): Long

    @Update
    suspend fun update(task: MaintenanceTaskEntity)

    @Query("""
        UPDATE maintenance_tasks
        SET lastPerformedDate = :date,
            lastPerformedOdometer = :odometer,
            nextDueDate = :nextDate,
            nextDueOdometer = :nextOdometer,
            updatedAt = :updatedAt
        WHERE id = :id
    """)
    suspend fun updateAfterLog(
        id: Long,
        date: Long,
        odometer: Int,
        nextDate: Long?,
        nextOdometer: Int?,
        updatedAt: Long
    )

    @Query("UPDATE maintenance_tasks SET isActive = 0, updatedAt = :updatedAt WHERE id = :id")
    suspend fun deactivate(id: Long, updatedAt: Long)
}
