package com.moto.tracker.domain.usecase.kmlog

import com.moto.tracker.domain.model.KmLog
import com.moto.tracker.domain.repository.MaintenanceRepository
import com.moto.tracker.domain.repository.VehicleRepository
import java.util.Calendar
import javax.inject.Inject

class LogDailyKmUseCase @Inject constructor(
    private val maintenanceRepository: MaintenanceRepository,
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(
        vehicleId: Long,
        odometer: Int,
        notes: String? = null
    ): Result<KmLog> = runCatching {
        val vehicle = vehicleRepository.getById(vehicleId)
            ?: error("Vehicle $vehicleId not found")

        val today = startOfDay(System.currentTimeMillis())
        val previous = maintenanceRepository.getLatestKmLog(vehicleId)
        val kmDriven = maxOf(0, odometer - (previous?.odometer ?: vehicle.currentOdometer))

        val log = KmLog(
            vehicleId = vehicleId,
            logDate = today,
            odometer = odometer,
            kmDriven = kmDriven,
            notes = notes,
            createdAt = System.currentTimeMillis()
        )

        maintenanceRepository.insertKmLog(log)

        // Update vehicle odometer if higher
        if (odometer > vehicle.currentOdometer) {
            vehicleRepository.updateOdometer(vehicleId, odometer)
        }

        log
    }

    private fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
