package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.FuelLogDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.data.local.mapper.toEntity
import com.moto.tracker.domain.model.FuelLog
import com.moto.tracker.domain.repository.FuelRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FuelRepositoryImpl @Inject constructor(
    private val dao: FuelLogDao
) : FuelRepository {

    override fun observeByVehicle(vehicleId: Long): Flow<List<FuelLog>> =
        dao.observeByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): FuelLog? = dao.getById(id)?.toDomain()

    override suspend fun getByVehicleAndDateRange(vehicleId: Long, from: Long, to: Long): List<FuelLog> =
        dao.getByVehicleAndDateRange(vehicleId, from, to).map { it.toDomain() }

    override suspend fun getLastFullFillUps(vehicleId: Long, limit: Int): List<FuelLog> =
        dao.getLastFullFillUps(vehicleId, limit).map { it.toDomain() }

    override suspend fun getAverageKmPerLiter(vehicleId: Long): Double? =
        dao.getAverageKmPerLiter(vehicleId)

    override suspend fun getLatestByVehicle(vehicleId: Long): FuelLog? =
        dao.getLatestByVehicle(vehicleId)?.toDomain()

    override suspend fun insert(log: FuelLog): Long = dao.insert(log.toEntity())

    override suspend fun update(log: FuelLog) = dao.update(log.toEntity())

    override suspend fun delete(log: FuelLog) = dao.delete(log.toEntity())
}
