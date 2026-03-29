package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.SeasonalChecklistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonalChecklistDao {
    @Query("SELECT * FROM seasonal_checklists ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<SeasonalChecklistEntity>>

    @Query("SELECT * FROM seasonal_checklists WHERE isTemplate = 1")
    fun observeTemplates(): Flow<List<SeasonalChecklistEntity>>

    @Query("SELECT * FROM seasonal_checklists WHERE vehicleId = :vehicleId")
    fun observeByVehicle(vehicleId: Long): Flow<List<SeasonalChecklistEntity>>

    @Query("SELECT * FROM seasonal_checklists WHERE id = :id")
    suspend fun getById(id: Long): SeasonalChecklistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(checklist: SeasonalChecklistEntity): Long

    @Query("SELECT COUNT(*) FROM seasonal_checklists WHERE isTemplate = 1")
    suspend fun countTemplates(): Int

    @Delete
    suspend fun delete(checklist: SeasonalChecklistEntity)
}
