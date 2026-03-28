package com.moto.tracker.domain.model

data class KmLog(
    val id: Long = 0,
    val vehicleId: Long,
    val logDate: Long,
    val odometer: Int,
    val kmDriven: Int,
    val notes: String? = null,
    val createdAt: Long
)
