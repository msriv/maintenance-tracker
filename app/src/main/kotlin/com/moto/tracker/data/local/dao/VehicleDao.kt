package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun observeById(id: Long): Flow<VehicleEntity?>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getById(id: Long): VehicleEntity?

    @Query("SELECT * FROM vehicles")
    suspend fun getAll(): List<VehicleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: VehicleEntity): Long

    @Update
    suspend fun update(vehicle: VehicleEntity)

    @Delete
    suspend fun delete(vehicle: VehicleEntity)

    @Query("UPDATE vehicles SET currentOdometer = :odometer, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateOdometer(id: Long, odometer: Int, updatedAt: Long)

    @Query("SELECT COUNT(*) FROM vehicles")
    suspend fun count(): Int
}
