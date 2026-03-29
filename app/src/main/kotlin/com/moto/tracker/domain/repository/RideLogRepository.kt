package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.RideLog
import kotlinx.coroutines.flow.Flow

interface RideLogRepository {
    fun observeByVehicle(vehicleId: Long): Flow<List<RideLog>>
    suspend fun getTotalKmSince(vehicleId: Long, since: Long): Int
    suspend fun getRecent(vehicleId: Long): List<RideLog>
    suspend fun insert(ride: RideLog): Long
    suspend fun delete(ride: RideLog)
}
