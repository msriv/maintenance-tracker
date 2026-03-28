package com.moto.tracker.domain.usecase.maintenance

import com.moto.tracker.domain.model.MaintenanceTask
import com.moto.tracker.domain.repository.MaintenanceRepository
import com.moto.tracker.domain.repository.VehicleRepository
import javax.inject.Inject

class AddMaintenanceTaskUseCase @Inject constructor(
    private val maintenanceRepository: MaintenanceRepository,
    private val vehicleRepository: VehicleRepository,
    private val computeNextDueDate: ComputeNextDueDateUseCase
) {
    suspend operator fun invoke(task: MaintenanceTask): Result<Long> = runCatching {
        val vehicle = vehicleRepository.getById(task.vehicleId)
            ?: error("Vehicle ${task.vehicleId} not found")

        val nextDue = computeNextDueDate(
            lastDate = task.lastPerformedDate,
            lastOdometer = task.lastPerformedOdometer,
            currentOdometer = vehicle.currentOdometer,
            thresholdDays = task.thresholdDays,
            thresholdKm = task.thresholdKm
        )

        val taskWithNextDue = task.copy(
            nextDueDate = nextDue.nextDueDate,
            nextDueOdometer = nextDue.nextDueOdometer
        )
        maintenanceRepository.insertTask(taskWithNextDue)
    }
}
