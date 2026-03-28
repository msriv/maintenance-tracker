package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.ManufacturerSchedule
import kotlinx.coroutines.flow.Flow

interface ManufacturerScheduleRepository {
    suspend fun getDistinctMakes(): List<String>
    suspend fun getModelsForMake(make: String): List<String>
    fun observeScheduleForVehicle(make: String, model: String, year: Int): Flow<List<ManufacturerSchedule>>
    suspend fun count(): Int
}
