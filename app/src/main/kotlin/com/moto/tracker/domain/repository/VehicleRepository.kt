package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun observeAll(): Flow<List<Vehicle>>
    fun observeById(id: Long): Flow<Vehicle?>
    suspend fun getById(id: Long): Vehicle?
    suspend fun getAll(): List<Vehicle>
    suspend fun insert(vehicle: Vehicle): Long
    suspend fun update(vehicle: Vehicle)
    suspend fun delete(vehicle: Vehicle)
    suspend fun updateOdometer(id: Long, odometer: Int)
    suspend fun count(): Int
}
