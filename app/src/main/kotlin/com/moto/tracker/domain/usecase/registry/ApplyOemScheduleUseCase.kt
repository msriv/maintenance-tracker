package com.moto.tracker.domain.usecase.registry

import com.moto.tracker.domain.model.MaintenanceTask
import com.moto.tracker.domain.model.MaintenanceTaskType
import com.moto.tracker.domain.repository.MaintenanceRepository
import com.moto.tracker.domain.repository.RegistryRepository
import javax.inject.Inject

class ApplyOemScheduleUseCase @Inject constructor(
    private val registryRepo: RegistryRepository,
    private val maintenanceRepo: MaintenanceRepository
) {
    suspend operator fun invoke(vehicleId: Long, make: String, model: String, year: Int): Result<Int> {
        return runCatching {
            val schedules = registryRepo.getSchedules(make, model, year)
            val now = System.currentTimeMillis()
            var insertedCount = 0

            schedules.forEach { schedule ->
                val taskType = mapTaskType(schedule.taskType)
                val task = MaintenanceTask(
                    vehicleId = vehicleId,
                    taskType = taskType,
                    customName = if (taskType == MaintenanceTaskType.CUSTOM) schedule.taskLabel else null,
                    thresholdDays = schedule.intervalDays,
                    thresholdKm = schedule.intervalKm,
                    notes = buildString {
                        schedule.notes?.let { append(it) }
                        if (schedule.source.isNotBlank()) {
                            if (isNotEmpty()) append("\n")
                            append("Source: ${schedule.source}")
                        }
                    }.takeIf { it.isNotBlank() },
                    createdAt = now,
                    updatedAt = now
                )
                maintenanceRepo.insertTask(task)
                insertedCount++
            }

            insertedCount
        }
    }

    private fun mapTaskType(taskType: String): MaintenanceTaskType {
        return when (taskType.uppercase()) {
            "ENGINE_OIL", "OIL_CHANGE" -> MaintenanceTaskType.ENGINE_OIL
            "AIR_FILTER" -> MaintenanceTaskType.AIR_FILTER
            "BRAKE_OIL", "BRAKE_FLUID" -> MaintenanceTaskType.BRAKE_OIL
            "CHAIN_LUBE", "CHAIN_LUBRICATION" -> MaintenanceTaskType.CHAIN_LUBE
            "CHAIN_ADJUSTMENT" -> MaintenanceTaskType.CHAIN_ADJUSTMENT
            "TIRE_ROTATION", "TYRE_ROTATION" -> MaintenanceTaskType.TIRE_ROTATION
            "TIRE_REPLACEMENT", "TYRE_REPLACEMENT" -> MaintenanceTaskType.TIRE_REPLACEMENT
            "BRAKE_PAD" -> MaintenanceTaskType.BRAKE_PAD
            "SPARK_PLUG" -> MaintenanceTaskType.SPARK_PLUG
            "COOLANT" -> MaintenanceTaskType.COOLANT
            "TRANSMISSION_OIL" -> MaintenanceTaskType.TRANSMISSION_OIL
            "BATTERY" -> MaintenanceTaskType.BATTERY
            else -> MaintenanceTaskType.CUSTOM
        }
    }
}
