package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.BestPracticeEntity
import com.moto.tracker.data.local.entity.RegistryMakeEntity
import com.moto.tracker.data.local.entity.RegistryModelEntity
import com.moto.tracker.data.local.entity.RegistryScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistryCacheDao {
    // Makes
    @Query("SELECT * FROM registry_makes ORDER BY name ASC")
    fun observeMakes(): Flow<List<RegistryMakeEntity>>

    @Query("SELECT * FROM registry_makes ORDER BY name ASC")
    suspend fun getMakes(): List<RegistryMakeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMakes(makes: List<RegistryMakeEntity>)

    @Query("DELETE FROM registry_makes")
    suspend fun clearMakes()

    // Models
    @Query("SELECT * FROM registry_models WHERE make = :make ORDER BY model ASC")
    fun observeModelsByMake(make: String): Flow<List<RegistryModelEntity>>

    @Query("SELECT * FROM registry_models WHERE make = :make ORDER BY model ASC")
    suspend fun getModelsByMake(make: String): List<RegistryModelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertModels(models: List<RegistryModelEntity>)

    @Query("DELETE FROM registry_models")
    suspend fun clearModels()

    // Schedules
    @Query("SELECT * FROM registry_schedules WHERE make = :make AND model = :model AND yearFrom <= :year AND (yearTo IS NULL OR yearTo >= :year)")
    suspend fun getSchedules(make: String, model: String, year: Int): List<RegistryScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSchedules(schedules: List<RegistryScheduleEntity>)

    @Query("DELETE FROM registry_schedules")
    suspend fun clearSchedules()

    // Best Practices
    @Query("SELECT * FROM best_practices WHERE isTemplate = 1")
    fun observeTemplates(): Flow<List<BestPracticeEntity>>

    @Query("SELECT * FROM best_practices")
    suspend fun getAll(): List<BestPracticeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBestPractices(practices: List<BestPracticeEntity>)

    @Query("DELETE FROM best_practices")
    suspend fun clearBestPractices()
}
