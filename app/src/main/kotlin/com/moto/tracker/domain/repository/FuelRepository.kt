package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.FuelLog
import kotlinx.coroutines.flow.Flow

interface FuelRepository {
    fun observeByVehicle(vehicleId: Long): Flow<List<FuelLog>>
    suspend fun getById(id: Long): FuelLog?
    suspend fun getByVehicleAndDateRange(vehicleId: Long, from: Long, to: Long): List<FuelLog>
    suspend fun getLastFullFillUps(vehicleId: Long, limit: Int): List<FuelLog>
    suspend fun getAverageKmPerLiter(vehicleId: Long): Double?
    suspend fun getLatestByVehicle(vehicleId: Long): FuelLog?
    suspend fun insert(log: FuelLog): Long
    suspend fun update(log: FuelLog)
    suspend fun delete(log: FuelLog)
}
