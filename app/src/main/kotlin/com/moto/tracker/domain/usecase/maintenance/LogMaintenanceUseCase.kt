package com.moto.tracker.domain.usecase.maintenance

import com.moto.tracker.domain.model.MaintenanceLog
import com.moto.tracker.domain.repository.MaintenanceRepository
import com.moto.tracker.domain.repository.VehicleRepository
import javax.inject.Inject

class LogMaintenanceUseCase @Inject constructor(
    private val maintenanceRepository: MaintenanceRepository,
    private val vehicleRepository: VehicleRepository,
    private val computeNextDueDate: ComputeNextDueDateUseCase
) {
    suspend operator fun invoke(log: MaintenanceLog): Result<Unit> = runCatching {
        // Insert the log entry
        maintenanceRepository.insertLog(log)

        // Get the task to compute next due
        val task = maintenanceRepository.getTaskById(log.taskId)
            ?: error("Task ${log.taskId} not found")

        val nextDue = computeNextDueDate(
            lastDate = log.performedDate,
            lastOdometer = log.odometer,
            currentOdometer = log.odometer,
            thresholdDays = task.thresholdDays,
            thresholdKm = task.thresholdKm
        )

        // Update the task's last performed + next due
        maintenanceRepository.updateTaskAfterLog(
            id = task.id,
            date = log.performedDate,
            odometer = log.odometer,
            nextDueResult = nextDue
        )

        // Update vehicle odometer if this log's odometer is higher
        val vehicle = vehicleRepository.getById(log.vehicleId)
        if (vehicle != null && log.odometer > vehicle.currentOdometer) {
            vehicleRepository.updateOdometer(log.vehicleId, log.odometer)
        }
    }
}
