package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.ManufacturerScheduleDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.domain.model.ManufacturerSchedule
import com.moto.tracker.domain.repository.ManufacturerScheduleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ManufacturerScheduleRepositoryImpl @Inject constructor(
    private val dao: ManufacturerScheduleDao
) : ManufacturerScheduleRepository {

    override suspend fun getDistinctMakes(): List<String> = dao.getDistinctMakes()

    override suspend fun getModelsForMake(make: String): List<String> = dao.getModelsForMake(make)

    override fun observeScheduleForVehicle(make: String, model: String, year: Int): Flow<List<ManufacturerSchedule>> =
        dao.observeScheduleForVehicle(make, model, year).map { list -> list.map { it.toDomain() } }

    override suspend fun count(): Int = dao.count()
}
