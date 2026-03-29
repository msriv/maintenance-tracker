package com.moto.tracker.domain.usecase.prediction

import com.moto.tracker.data.local.dao.KmLogDao
import com.moto.tracker.domain.model.MileagePrediction
import com.moto.tracker.domain.repository.MaintenanceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class GetMileagePredictionUseCase @Inject constructor(
    private val kmLogDao: KmLogDao,
    private val maintenanceRepo: MaintenanceRepository,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(vehicleId: Long): MileagePrediction? = withContext(ioDispatcher) {
        val ninetyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(90)
        val recentLogs = kmLogDao.getRecentByVehicle(vehicleId, 90)
            .filter { it.logDate >= ninetyDaysAgo }
            .sortedBy { it.logDate }

        if (recentLogs.size < 3) return@withContext null

        // Calculate total km driven over the period
        val totalKm = recentLogs.sumOf { it.kmDriven }
        val firstDate = recentLogs.first().logDate
        val lastDate = recentLogs.last().logDate
        val daysSpan = TimeUnit.MILLISECONDS.toDays(lastDate - firstDate).coerceAtLeast(1)

        val avgKmPerDay = totalKm.toDouble() / daysSpan
        val avgKmPerWeek = avgKmPerDay * 7
        val avgKmPerMonth = avgKmPerDay * 30

        // Confidence based on data density (entries per week)
        val weeksSpan = (daysSpan / 7.0).coerceAtLeast(1.0)
        val entriesPerWeek = recentLogs.size / weeksSpan
        val confidenceScore = (entriesPerWeek / 5.0).toFloat().coerceIn(0f, 1f)

        // Find the nearest upcoming maintenance task by km threshold
        val activeTasks = maintenanceRepo.getActiveTasksByVehicle(vehicleId)
        val latestLog = recentLogs.lastOrNull()
        val currentOdometer = latestLog?.odometer ?: 0

        val nearestTask = activeTasks
            .filter { task -> task.nextDueOdometer != null && task.nextDueOdometer > currentOdometer }
            .minByOrNull { task -> task.nextDueOdometer!! }

        val projectedNextServiceOdometer = nearestTask?.nextDueOdometer
        val daysUntilService = projectedNextServiceOdometer?.let { targetOdometer ->
            val kmRemaining = targetOdometer - currentOdometer
            if (avgKmPerDay > 0) (kmRemaining / avgKmPerDay).toInt() else null
        }
        val projectedNextServiceDate = daysUntilService?.let { days ->
            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days.toLong())
        }

        MileagePrediction(
            vehicleId = vehicleId,
            avgKmPerMonth = avgKmPerMonth,
            avgKmPerWeek = avgKmPerWeek,
            projectedNextServiceDate = projectedNextServiceDate,
            projectedNextServiceOdometer = projectedNextServiceOdometer,
            daysUntilService = daysUntilService,
            confidenceScore = confidenceScore
        )
    }
}
