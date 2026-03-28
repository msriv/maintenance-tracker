package com.moto.tracker.data.local.dao

import androidx.room.*
import com.moto.tracker.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE vehicleId = :vehicleId ORDER BY createdAt DESC")
    fun observeByVehicle(vehicleId: Long): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE vehicleId = :vehicleId AND type = :type ORDER BY createdAt DESC")
    fun observeByVehicleAndType(vehicleId: Long, type: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getById(id: Long): DocumentEntity?

    @Query("SELECT * FROM documents WHERE expiryDate IS NOT NULL AND expiryDate < :timestamp")
    suspend fun getExpiredDocuments(timestamp: Long): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE expiryDate IS NOT NULL AND expiryDate BETWEEN :from AND :to")
    suspend fun getDocumentsExpiringBetween(from: Long, to: Long): List<DocumentEntity>

    @Query("SELECT * FROM documents WHERE vehicleId = :vehicleId AND expiryDate IS NOT NULL AND expiryDate BETWEEN :from AND :to")
    suspend fun getExpiringForVehicle(vehicleId: Long, from: Long, to: Long): List<DocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(document: DocumentEntity): Long

    @Update
    suspend fun update(document: DocumentEntity)

    @Delete
    suspend fun delete(document: DocumentEntity)
}
