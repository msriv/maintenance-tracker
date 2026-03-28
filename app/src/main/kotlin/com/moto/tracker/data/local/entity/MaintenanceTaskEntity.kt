package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "maintenance_tasks",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId")]
)
data class MaintenanceTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val taskType: String,               // MaintenanceTaskType enum name
    val customName: String? = null,
    val thresholdDays: Int? = null,
    val thresholdKm: Int? = null,
    val lastPerformedDate: Long? = null,
    val lastPerformedOdometer: Int? = null,
    val nextDueDate: Long? = null,
    val nextDueOdometer: Int? = null,
    val isActive: Boolean = true,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
