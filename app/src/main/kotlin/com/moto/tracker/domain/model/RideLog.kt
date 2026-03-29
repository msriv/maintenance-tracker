package com.moto.tracker.domain.model

data class RideLog(
    val id: Long = 0,
    val vehicleId: Long,
    val date: Long,
    val startOdometer: Int,
    val endOdometer: Int,
    val kmDriven: Int,
    val tripName: String? = null,
    val notes: String? = null,
    val createdAt: Long
)
