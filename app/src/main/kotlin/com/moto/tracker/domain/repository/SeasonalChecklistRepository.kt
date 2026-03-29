package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.SeasonalChecklist
import kotlinx.coroutines.flow.Flow

interface SeasonalChecklistRepository {
    fun observeAll(): Flow<List<SeasonalChecklist>>
    fun observeTemplates(): Flow<List<SeasonalChecklist>>
    fun observeByVehicle(vehicleId: Long): Flow<List<SeasonalChecklist>>
    suspend fun getById(id: Long): SeasonalChecklist?
    suspend fun upsert(checklist: SeasonalChecklist): Long
    suspend fun delete(checklist: SeasonalChecklist)
}
