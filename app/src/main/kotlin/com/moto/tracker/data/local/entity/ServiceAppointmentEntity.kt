package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_appointments",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId"), Index("appointmentDate")]
)
data class ServiceAppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val appointmentDate: Long,
    val title: String,
    val serviceCenterName: String? = null,
    val serviceCenterAddress: String? = null,
    val serviceCenterPhone: String? = null,
    val taskType: String? = null,
    val estimatedCost: Double? = null,
    val actualCost: Double? = null,
    val status: String = "upcoming",    // AppointmentStatus enum name
    val reminderEnabled: Boolean = true,
    val reminderMinutesBefore: Int = 1440,  // 1 day default
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
