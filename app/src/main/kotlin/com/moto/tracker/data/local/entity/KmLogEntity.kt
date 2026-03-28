package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "km_logs",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId"), Index("logDate")]
)
data class KmLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val logDate: Long,          // epoch millis, normalized to start-of-day UTC
    val odometer: Int,          // absolute reading
    val kmDriven: Int,          // delta from previous reading
    val notes: String? = null,
    val createdAt: Long
)
