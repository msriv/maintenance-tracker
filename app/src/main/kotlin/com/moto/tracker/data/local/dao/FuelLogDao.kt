package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.FuelLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelLogDao {
    @Query("SELECT * FROM fuel_logs WHERE vehicleId = :vehicleId ORDER BY fillDate DESC")
    fun observeByVehicle(vehicleId: Long): Flow<List<FuelLogEntity>>

    @Query("SELECT * FROM fuel_logs WHERE id = :id")
    suspend fun getById(id: Long): FuelLogEntity?

    @Query("SELECT * FROM fuel_logs WHERE vehicleId = :vehicleId AND fillDate BETWEEN :from AND :to ORDER BY fillDate ASC")
    suspend fun getByVehicleAndDateRange(vehicleId: Long, from: Long, to: Long): List<FuelLogEntity>

    @Query("SELECT * FROM fuel_logs WHERE vehicleId = :vehicleId AND isTankFull = 1 ORDER BY fillDate DESC LIMIT :limit")
    suspend fun getLastFullFillUps(vehicleId: Long, limit: Int): List<FuelLogEntity>

    @Query("SELECT AVG(kmPerLiter) FROM fuel_logs WHERE vehicleId = :vehicleId AND kmPerLiter IS NOT NULL")
    suspend fun getAverageKmPerLiter(vehicleId: Long): Double?

    @Query("SELECT * FROM fuel_logs WHERE vehicleId = :vehicleId ORDER BY fillDate DESC LIMIT 1")
    suspend fun getLatestByVehicle(vehicleId: Long): FuelLogEntity?

    @Insert
    suspend fun insert(log: FuelLogEntity): Long

    @Update
    suspend fun update(log: FuelLogEntity)

    @Delete
    suspend fun delete(log: FuelLogEntity)
}
