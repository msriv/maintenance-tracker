package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "maintenance_logs",
    foreignKeys = [
        ForeignKey(
            entity = MaintenanceTaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("taskId"), Index("vehicleId")]
)
data class MaintenanceLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val vehicleId: Long,        // denormalized for query performance
    val performedDate: Long,
    val odometer: Int,
    val cost: Double? = null,
    val mechanicName: String? = null,
    val serviceCenterName: String? = null,
    val notes: String? = null,
    val createdAt: Long
)
