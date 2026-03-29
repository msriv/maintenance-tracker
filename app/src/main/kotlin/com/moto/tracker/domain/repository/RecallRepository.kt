package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.RecallAlert
import kotlinx.coroutines.flow.Flow

interface RecallRepository {
    fun observeByVehicle(vehicleId: Long): Flow<List<RecallAlert>>
    fun observeUnacknowledged(vehicleId: Long): Flow<List<RecallAlert>>
    fun observeUnacknowledgedCount(vehicleId: Long): Flow<Int>
    suspend fun fetchAndStoreRecalls(vehicleId: Long, make: String, model: String, year: Int): Result<Int>
    suspend fun acknowledge(recallId: Long)
}
