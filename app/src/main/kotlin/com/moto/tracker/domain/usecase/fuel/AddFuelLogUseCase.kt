package com.moto.tracker.domain.usecase.fuel

import com.moto.tracker.domain.model.FuelLog
import com.moto.tracker.domain.repository.FuelRepository
import com.moto.tracker.domain.repository.VehicleRepository
import javax.inject.Inject

class AddFuelLogUseCase @Inject constructor(
    private val fuelRepository: FuelRepository,
    private val vehicleRepository: VehicleRepository,
    private val computeKmPerLiter: ComputeKmPerLiterUseCase
) {
    suspend operator fun invoke(log: FuelLog): Result<Long> = runCatching {
        // Compute km/L if this is a full fill-up
        val kmPerLiter: Double? = if (log.isTankFull) {
            val previousFull = fuelRepository.getLastFullFillUps(log.vehicleId, limit = 1)
                .firstOrNull()
            if (previousFull != null) {
                computeKmPerLiter.computeFromLogs(older = previousFull, newer = log)
            } else null
        } else null

        val logWithEfficiency = log.copy(kmPerLiter = kmPerLiter)
        val id = fuelRepository.insert(logWithEfficiency)

        // Update vehicle odometer if higher
        val vehicle = vehicleRepository.getById(log.vehicleId)
        if (vehicle != null && log.odometer > vehicle.currentOdometer) {
            vehicleRepository.updateOdometer(log.vehicleId, log.odometer)
        }

        id
    }
}
