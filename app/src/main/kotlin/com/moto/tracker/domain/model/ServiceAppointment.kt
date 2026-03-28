package com.moto.tracker.domain.model

data class ServiceAppointment(
    val id: Long = 0,
    val vehicleId: Long,
    val appointmentDate: Long,
    val title: String,
    val serviceCenterName: String? = null,
    val serviceCenterAddress: String? = null,
    val serviceCenterPhone: String? = null,
    val taskType: String? = null,
    val estimatedCost: Double? = null,
    val actualCost: Double? = null,
    val status: AppointmentStatus = AppointmentStatus.UPCOMING,
    val reminderEnabled: Boolean = true,
    val reminderMinutesBefore: Int = 1440,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)

enum class AppointmentStatus(val displayName: String) {
    UPCOMING("Upcoming"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled")
}
