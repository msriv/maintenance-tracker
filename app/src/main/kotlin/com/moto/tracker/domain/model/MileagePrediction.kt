package com.moto.tracker.domain.model

data class MileagePrediction(
    val vehicleId: Long,
    val avgKmPerMonth: Double,
    val avgKmPerWeek: Double,
    val projectedNextServiceDate: Long?,    // epoch millis, null if no km threshold
    val projectedNextServiceOdometer: Int?, // predicted odometer at next service
    val daysUntilService: Int?,
    val confidenceScore: Float  // 0.0 - 1.0, higher = more data points
)
