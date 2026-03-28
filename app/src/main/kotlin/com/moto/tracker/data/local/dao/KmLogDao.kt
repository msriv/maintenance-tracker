package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.KmLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KmLogDao {
    @Query("SELECT * FROM km_logs WHERE vehicleId = :vehicleId ORDER BY logDate DESC")
    fun observeByVehicle(vehicleId: Long): Flow<List<KmLogEntity>>

    @Query("SELECT * FROM km_logs WHERE vehicleId = :vehicleId AND logDate = :date LIMIT 1")
    suspend fun getByVehicleAndDate(vehicleId: Long, date: Long): KmLogEntity?

    @Query("SELECT * FROM km_logs WHERE vehicleId = :vehicleId ORDER BY logDate DESC LIMIT 1")
    suspend fun getLatestByVehicle(vehicleId: Long): KmLogEntity?

    @Query("SELECT SUM(kmDriven) FROM km_logs WHERE vehicleId = :vehicleId AND logDate BETWEEN :from AND :to")
    suspend fun getTotalKmInRange(vehicleId: Long, from: Long, to: Long): Int?

    @Query("SELECT * FROM km_logs WHERE vehicleId = :vehicleId ORDER BY logDate DESC LIMIT :limit")
    suspend fun getRecentByVehicle(vehicleId: Long, limit: Int): List<KmLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: KmLogEntity): Long

    @Delete
    suspend fun delete(log: KmLogEntity)
}
