package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.ManufacturerScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ManufacturerScheduleDao {
    @Query("SELECT DISTINCT make FROM manufacturer_schedules ORDER BY make ASC")
    suspend fun getDistinctMakes(): List<String>

    @Query("SELECT DISTINCT model FROM manufacturer_schedules WHERE make = :make ORDER BY model ASC")
    suspend fun getModelsForMake(make: String): List<String>

    @Query("""
        SELECT * FROM manufacturer_schedules
        WHERE make = :make AND model = :model
        AND yearFrom <= :year
        AND (yearTo IS NULL OR yearTo >= :year)
        ORDER BY taskLabel ASC
    """)
    fun observeScheduleForVehicle(make: String, model: String, year: Int): Flow<List<ManufacturerScheduleEntity>>

    @Query("SELECT COUNT(*) FROM manufacturer_schedules")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(schedules: List<ManufacturerScheduleEntity>)
}
