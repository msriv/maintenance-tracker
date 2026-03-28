package com.moto.tracker.domain.usecase.fuel

import com.moto.tracker.domain.model.FuelLog
import com.moto.tracker.domain.repository.FuelRepository
import javax.inject.Inject

/**
 * Tank-to-tank fuel efficiency calculation.
 *
 * Algorithm:
 * 1. Find the last two consecutive full fill-ups (isTankFull = true).
 * 2. km/L = (odometer_at_second_fill - odometer_at_first_fill) / liters_at_second_fill
 *
 * Only full tank fill-ups are used to ensure accuracy.
 * Returns null if fewer than two full fill-ups exist.
 */
class ComputeKmPerLiterUseCase @Inject constructor(
    private val fuelRepository: FuelRepository
) {
    suspend operator fun invoke(vehicleId: Long): Double? {
        val lastTwoFull = fuelRepository.getLastFullFillUps(vehicleId, limit = 2)
        if (lastTwoFull.size < 2) return null

        val newerFill = lastTwoFull[0]   // most recent
        val olderFill = lastTwoFull[1]   // previous

        val kmDriven = newerFill.odometer - olderFill.odometer
        if (kmDriven <= 0 || newerFill.liters <= 0.0) return null

        return kmDriven.toDouble() / newerFill.liters
    }

    /**
     * Computes km/L from two consecutive fill-up entries directly (for immediate use after insert).
     */
    fun computeFromLogs(older: FuelLog, newer: FuelLog): Double? {
        val kmDriven = newer.odometer - older.odometer
        if (kmDriven <= 0 || newer.liters <= 0.0) return null
        return kmDriven.toDouble() / newer.liters
    }
}
