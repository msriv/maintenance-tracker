package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.RecallAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecallAlertDao {
    @Query("SELECT * FROM recall_alerts WHERE vehicleId = :vehicleId ORDER BY createdAt DESC")
    fun observeByVehicle(vehicleId: Long): Flow<List<RecallAlertEntity>>

    @Query("SELECT * FROM recall_alerts WHERE vehicleId = :vehicleId AND isAcknowledged = 0")
    fun observeUnacknowledgedByVehicle(vehicleId: Long): Flow<List<RecallAlertEntity>>

    @Query("SELECT COUNT(*) FROM recall_alerts WHERE vehicleId = :vehicleId AND isAcknowledged = 0")
    fun observeUnacknowledgedCount(vehicleId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(recalls: List<RecallAlertEntity>)

    @Query("UPDATE recall_alerts SET isAcknowledged = 1, updatedAt = :updatedAt WHERE id = :id")
    suspend fun acknowledge(id: Long, updatedAt: Long)

    @Query("SELECT * FROM recall_alerts WHERE campaignId = :campaignId AND vehicleId = :vehicleId LIMIT 1")
    suspend fun getByCampaignAndVehicle(campaignId: String, vehicleId: Long): RecallAlertEntity?

    @Query("DELETE FROM recall_alerts WHERE vehicleId = :vehicleId")
    suspend fun deleteByVehicle(vehicleId: Long)
}
