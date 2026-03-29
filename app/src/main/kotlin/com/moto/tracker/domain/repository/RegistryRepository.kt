package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.BestPractice
import com.moto.tracker.domain.model.RegistryMake
import com.moto.tracker.domain.model.RegistryModel
import com.moto.tracker.domain.model.RegistrySchedule
import kotlinx.coroutines.flow.Flow

interface RegistryRepository {
    fun observeMakes(): Flow<List<RegistryMake>>
    fun observeModelsByMake(make: String): Flow<List<RegistryModel>>
    fun observeBestPracticeTemplates(): Flow<List<BestPractice>>
    suspend fun getMakes(): List<RegistryMake>
    suspend fun getModelsByMake(make: String): List<RegistryModel>
    suspend fun getSchedules(make: String, model: String, year: Int): List<RegistrySchedule>
    suspend fun syncFromFirestore(): Result<Unit>
    suspend fun getLastSyncTime(): Long?
}
