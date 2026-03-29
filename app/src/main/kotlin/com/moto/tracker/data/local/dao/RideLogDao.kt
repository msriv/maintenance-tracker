package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.RideLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RideLogDao {
    @Query("SELECT * FROM ride_logs WHERE vehicleId = :vehicleId ORDER BY date DESC")
    fun observeByVehicle(vehicleId: Long): Flow<List<RideLogEntity>>

    @Query("SELECT SUM(kmDriven) FROM ride_logs WHERE vehicleId = :vehicleId AND date >= :since")
    suspend fun getTotalKmSince(vehicleId: Long, since: Long): Int?

    @Query("SELECT * FROM ride_logs WHERE vehicleId = :vehicleId ORDER BY date DESC LIMIT 30")
    suspend fun getRecent(vehicleId: Long): List<RideLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ride: RideLogEntity): Long

    @Delete
    suspend fun delete(ride: RideLogEntity)
}
