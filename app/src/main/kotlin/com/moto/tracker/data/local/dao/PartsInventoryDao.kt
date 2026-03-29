package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.PartsInventoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PartsInventoryDao {
    @Query("SELECT * FROM parts_inventory WHERE vehicleId = :vehicleId ORDER BY partName ASC")
    fun observeByVehicle(vehicleId: Long): Flow<List<PartsInventoryEntity>>

    @Query("SELECT * FROM parts_inventory WHERE vehicleId = :vehicleId AND quantity < minQuantity")
    fun observeLowStock(vehicleId: Long): Flow<List<PartsInventoryEntity>>

    @Query("SELECT * FROM parts_inventory WHERE id = :id")
    suspend fun getById(id: Long): PartsInventoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(part: PartsInventoryEntity): Long

    @Delete
    suspend fun delete(part: PartsInventoryEntity)
}
