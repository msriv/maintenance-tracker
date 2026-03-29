package com.moto.tracker.data.local

import com.moto.tracker.data.local.dao.SeasonalChecklistDao
import com.moto.tracker.data.local.entity.SeasonalChecklistEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeasonalChecklistSeeder @Inject constructor(
    private val dao: SeasonalChecklistDao
) {
    suspend fun seedIfEmpty() {
        val existing = dao.observeTemplates()
        // Peek synchronously using first() equivalent — query count directly
        if (dao.countTemplates() > 0) return

        val now = System.currentTimeMillis()

        val templates = listOf(
            SeasonalChecklistEntity(
                vehicleId = null,
                name = "Summer Prep",
                season = "SUMMER",
                tasksJson = Json.encodeToString(
                    listOf(
                        "Check tire tread & pressure",
                        "Inspect brake pads",
                        "Check chain tension & lube",
                        "Check air filter",
                        "Test all lights",
                        "Check battery terminals",
                        "Inspect coolant level"
                    )
                ),
                isTemplate = true,
                isCompleted = false,
                createdAt = now,
                updatedAt = now
            ),
            SeasonalChecklistEntity(
                vehicleId = null,
                name = "Monsoon Prep",
                season = "MONSOON",
                tasksJson = Json.encodeToString(
                    listOf(
                        "Apply chain wax (not regular lube)",
                        "Check all electrical connections for corrosion",
                        "Inspect brake lines",
                        "Verify tire tread depth > 2mm",
                        "Apply corrosion inhibitor to exposed metal",
                        "Check wiper blades (if scooter)",
                        "Ensure drain holes in fairings are clear"
                    )
                ),
                isTemplate = true,
                isCompleted = false,
                createdAt = now,
                updatedAt = now
            ),
            SeasonalChecklistEntity(
                vehicleId = null,
                name = "Winter Storage",
                season = "WINTER",
                tasksJson = Json.encodeToString(
                    listOf(
                        "Change engine oil before storage",
                        "Fill fuel tank & add fuel stabilizer",
                        "Remove battery or connect trickle charger",
                        "Inflate tires to max PSI",
                        "Clean & wax all surfaces",
                        "Cover bike with breathable cover",
                        "Lubricate all cables & pivot points"
                    )
                ),
                isTemplate = true,
                isCompleted = false,
                createdAt = now,
                updatedAt = now
            )
        )

        templates.forEach { dao.upsert(it) }
    }
}
