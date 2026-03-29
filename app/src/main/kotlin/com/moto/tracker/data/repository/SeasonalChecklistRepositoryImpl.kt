package com.moto.tracker.data.repository

import com.moto.tracker.data.local.dao.SeasonalChecklistDao
import com.moto.tracker.data.local.mapper.toDomain
import com.moto.tracker.data.local.mapper.toEntity
import com.moto.tracker.domain.model.Season
import com.moto.tracker.domain.model.SeasonalChecklist
import com.moto.tracker.domain.repository.SeasonalChecklistRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class SeasonalChecklistRepositoryImpl @Inject constructor(
    private val dao: SeasonalChecklistDao,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : SeasonalChecklistRepository {

    override fun observeAll(): Flow<List<SeasonalChecklist>> =
        dao.observeAll().map { list -> list.map { it.toDomain() } }

    override fun observeTemplates(): Flow<List<SeasonalChecklist>> =
        dao.observeTemplates().map { list -> list.map { it.toDomain() } }

    override fun observeByVehicle(vehicleId: Long): Flow<List<SeasonalChecklist>> =
        dao.observeByVehicle(vehicleId).map { list -> list.map { it.toDomain() } }

    override suspend fun getById(id: Long): SeasonalChecklist? = dao.getById(id)?.toDomain()

    override suspend fun upsert(checklist: SeasonalChecklist): Long = dao.upsert(checklist.toEntity())

    override suspend fun delete(checklist: SeasonalChecklist) = dao.delete(checklist.toEntity())

    suspend fun seedDefaultTemplatesIfEmpty() = withContext(ioDispatcher) {
        val now = System.currentTimeMillis()

        val templates = listOf(
            SeasonalChecklist(
                vehicleId = null,
                name = "Summer Prep",
                season = Season.SUMMER,
                tasks = listOf(
                    "Check tire tread & pressure",
                    "Inspect brake pads",
                    "Check chain tension & lube",
                    "Check air filter",
                    "Test all lights",
                    "Check battery terminals",
                    "Inspect coolant level"
                ),
                isTemplate = true,
                isCompleted = false,
                createdAt = now,
                updatedAt = now
            ),
            SeasonalChecklist(
                vehicleId = null,
                name = "Monsoon Prep",
                season = Season.MONSOON,
                tasks = listOf(
                    "Apply chain wax (not regular lube)",
                    "Check all electrical connections for corrosion",
                    "Inspect brake lines",
                    "Verify tire tread depth > 2mm",
                    "Apply corrosion inhibitor to exposed metal",
                    "Check wiper blades (if scooter)",
                    "Ensure drain holes in fairings are clear"
                ),
                isTemplate = true,
                isCompleted = false,
                createdAt = now,
                updatedAt = now
            ),
            SeasonalChecklist(
                vehicleId = null,
                name = "Winter Storage",
                season = Season.WINTER,
                tasks = listOf(
                    "Change engine oil before storage",
                    "Fill fuel tank & add fuel stabilizer",
                    "Remove battery or connect trickle charger",
                    "Inflate tires to max PSI",
                    "Clean & wax all surfaces",
                    "Cover bike with breathable cover",
                    "Lubricate all cables & pivot points"
                ),
                isTemplate = true,
                isCompleted = false,
                createdAt = now,
                updatedAt = now
            )
        )

        templates.forEach { template ->
            dao.upsert(template.toEntity())
        }
    }
}
