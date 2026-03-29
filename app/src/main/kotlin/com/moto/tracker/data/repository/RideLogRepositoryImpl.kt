package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.RideLogDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.data.local.mapper.toEntity
import com.moto.tracker.domain.model.RideLog
import com.moto.tracker.domain.repository.RideLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RideLogRepositoryImpl @Inject constructor(
    private val dao: RideLogDao
) : RideLogRepository {

    override fun observeByVehicle(vehicleId: Long): Flow<List<RideLog>> =
        dao.observeByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override suspend fun getTotalKmSince(vehicleId: Long, since: Long): Int =
        dao.getTotalKmSince(vehicleId, since) ?: 0

    override suspend fun getRecent(vehicleId: Long): List<RideLog> =
        dao.getRecent(vehicleId).map { it.toDomain() }

    override suspend fun insert(ride: RideLog): Long = dao.insert(ride.toEntity())

    override suspend fun delete(ride: RideLog) = dao.delete(ride.toEntity())
}
