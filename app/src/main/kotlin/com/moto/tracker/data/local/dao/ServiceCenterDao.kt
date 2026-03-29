package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.ServiceCenterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceCenterDao {
    @Query("SELECT * FROM service_centers ORDER BY isFavorite DESC, name ASC")
    fun observeAll(): Flow<List<ServiceCenterEntity>>

    @Query("SELECT * FROM service_centers WHERE isFavorite = 1")
    fun observeFavorites(): Flow<List<ServiceCenterEntity>>

    @Query("SELECT * FROM service_centers WHERE id = :id")
    suspend fun getById(id: Long): ServiceCenterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(center: ServiceCenterEntity): Long

    @Delete
    suspend fun delete(center: ServiceCenterEntity)

    @Query("UPDATE service_centers SET isFavorite = :isFavorite, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean, updatedAt: Long)
}
