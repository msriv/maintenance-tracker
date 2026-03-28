package com.moto.tracker.domain.usecase.maintenance

import com.moto.tracker.domain.model.NextDueResult
import javax.inject.Inject

/**
 * Computes the next due date and odometer reading for a maintenance task.
 *
 * Rules:
 * - If both thresholdDays and thresholdKm are set, BOTH are computed (user is notified
 *   by whichever trigger fires first).
 * - If lastDate is null (never performed), nextDueDate = today (immediately due by date logic).
 * - If lastOdometer is null, nextDueOdometer = currentOdometer + threshold.
 */
class ComputeNextDueDateUseCase @Inject constructor() {
    operator fun invoke(
        lastDate: Long?,
        lastOdometer: Int?,
        currentOdometer: Int,
        thresholdDays: Int?,
        thresholdKm: Int?,
        nowMillis: Long = System.currentTimeMillis()
    ): NextDueResult {
        val nextByDate: Long? = thresholdDays?.let { days ->
            val base = lastDate ?: nowMillis
            base + days.toLong() * MILLIS_PER_DAY
        }

        val nextByKm: Int? = thresholdKm?.let { km ->
            val base = lastOdometer ?: currentOdometer
            base + km
        }

        val isDueByDate = nextByDate != null && nextByDate <= nowMillis
        val isDueByKm = nextByKm != null && nextByKm <= currentOdometer

        return NextDueResult(
            nextDueDate = nextByDate,
            nextDueOdometer = nextByKm,
            isDueByDate = isDueByDate,
            isDueByKm = isDueByKm
        )
    }

    private companion object {
        const val MILLIS_PER_DAY = 24L * 60 * 60 * 1000
    }
}
