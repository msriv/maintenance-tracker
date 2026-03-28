package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.Document
import com.moto.tracker.domain.model.DocumentType
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun observeByVehicle(vehicleId: Long): Flow<List<Document>>
    fun observeByVehicleAndType(vehicleId: Long, type: DocumentType): Flow<List<Document>>
    suspend fun getById(id: Long): Document?
    suspend fun getExpiredDocuments(timestamp: Long): List<Document>
    suspend fun getDocumentsExpiringBetween(from: Long, to: Long): List<Document>
    suspend fun insert(document: Document): Long
    suspend fun update(document: Document)
    suspend fun delete(document: Document)
}
