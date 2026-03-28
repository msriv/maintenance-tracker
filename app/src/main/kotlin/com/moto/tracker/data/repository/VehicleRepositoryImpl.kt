package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.VehicleDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.data.local.mapper.toEntity
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val dao: VehicleDao
) : VehicleRepository {

    override fun observeAll(): Flow<List<Vehicle>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Vehicle?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun getById(id: Long): Vehicle? = dao.getById(id)?.toDomain()

    override suspend fun getAll(): List<Vehicle> = dao.getAll().map { it.toDomain() }

    override suspend fun insert(vehicle: Vehicle): Long = dao.insert(vehicle.toEntity())

    override suspend fun update(vehicle: Vehicle) = dao.update(vehicle.toEntity())

    override suspend fun delete(vehicle: Vehicle) = dao.delete(vehicle.toEntity())

    override suspend fun updateOdometer(id: Long, odometer: Int) =
        dao.updateOdometer(id, odometer, System.currentTimeMillis())

    override suspend fun count(): Int = dao.count()
}
