package com.moto.tracker.domain.model

data class FuelLog(
    val id: Long = 0,
    val vehicleId: Long,
    val fillDate: Long,
    val odometer: Int,
    val liters: Double,
    val pricePerLiter: Double,
    val totalCost: Double,
    val fuelType: String,
    val stationName: String? = null,
    val isTankFull: Boolean = true,
    val kmPerLiter: Double? = null,
    val notes: String? = null,
    val createdAt: Long
)
