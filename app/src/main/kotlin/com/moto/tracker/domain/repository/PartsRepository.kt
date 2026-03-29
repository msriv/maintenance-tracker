package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.PartsInventory
import kotlinx.coroutines.flow.Flow

interface PartsRepository {
    fun observeByVehicle(vehicleId: Long): Flow<List<PartsInventory>>
    fun observeLowStock(vehicleId: Long): Flow<List<PartsInventory>>
    suspend fun getById(id: Long): PartsInventory?
    suspend fun upsert(part: PartsInventory): Long
    suspend fun delete(part: PartsInventory)
}
