package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.PartsInventoryDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.data.local.mapper.toEntity
import com.moto.tracker.domain.model.PartsInventory
import com.moto.tracker.domain.repository.PartsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PartsRepositoryImpl @Inject constructor(
    private val dao: PartsInventoryDao
) : PartsRepository {

    override fun observeByVehicle(vehicleId: Long): Flow<List<PartsInventory>> =
        dao.observeByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override fun observeLowStock(vehicleId: Long): Flow<List<PartsInventory>> =
        dao.observeLowStock(vehicleId).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): PartsInventory? = dao.getById(id)?.toDomain()

    override suspend fun upsert(part: PartsInventory): Long = dao.upsert(part.toEntity())

    override suspend fun delete(part: PartsInventory) = dao.delete(part.toEntity())
}
