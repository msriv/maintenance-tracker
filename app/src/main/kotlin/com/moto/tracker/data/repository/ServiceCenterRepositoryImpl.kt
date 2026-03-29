package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.ServiceCenterDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.data.local.mapper.toEntity
import com.moto.tracker.domain.model.ServiceCenter
import com.moto.tracker.domain.repository.ServiceCenterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ServiceCenterRepositoryImpl @Inject constructor(
    private val dao: ServiceCenterDao
) : ServiceCenterRepository {

    override fun observeAll(): Flow<List<ServiceCenter>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeFavorites(): Flow<List<ServiceCenter>> =
        dao.observeFavorites().map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): ServiceCenter? = dao.getById(id)?.toDomain()

    override suspend fun upsert(center: ServiceCenter): Long = dao.upsert(center.toEntity())

    override suspend fun delete(center: ServiceCenter) = dao.delete(center.toEntity())

    override suspend fun setFavorite(id: Long, isFavorite: Boolean) {
        dao.setFavorite(id, isFavorite, System.currentTimeMillis())
    }
}
