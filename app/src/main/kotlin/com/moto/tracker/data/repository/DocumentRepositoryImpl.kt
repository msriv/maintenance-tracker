package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.DocumentDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.data.local.mapper.toEntity
import com.moto.tracker.domain.model.Document
import com.moto.tracker.domain.model.DocumentType
import com.moto.tracker.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DocumentRepositoryImpl @Inject constructor(
    private val dao: DocumentDao
) : DocumentRepository {

    override fun observeByVehicle(vehicleId: Long): Flow<List<Document>> =
        dao.observeByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override fun observeByVehicleAndType(vehicleId: Long, type: DocumentType): Flow<List<Document>> =
        dao.observeByVehicleAndType(vehicleId, type.name).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): Document? = dao.getById(id)?.toDomain()

    override suspend fun getExpiredDocuments(timestamp: Long): List<Document> =
        dao.getExpiredDocuments(timestamp).map { it.toDomain() }

    override suspend fun getDocumentsExpiringBetween(from: Long, to: Long): List<Document> =
        dao.getDocumentsExpiringBetween(from, to).map { it.toDomain() }

    override suspend fun insert(document: Document): Long = dao.insert(document.toEntity())

    override suspend fun update(document: Document) = dao.update(document.toEntity())

    override suspend fun delete(document: Document) = dao.delete(document.toEntity())
}
