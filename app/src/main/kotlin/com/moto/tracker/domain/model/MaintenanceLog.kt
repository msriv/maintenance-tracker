package com.moto.tracker.domain.model

data class MaintenanceLog(
    val id: Long = 0,
    val taskId: Long,
    val vehicleId: Long,
    val performedDate: Long,
    val odometer: Int,
    val cost: Double? = null,
    val mechanicName: String? = null,
    val serviceCenterName: String? = null,
    val notes: String? = null,
    val createdAt: Long
)
