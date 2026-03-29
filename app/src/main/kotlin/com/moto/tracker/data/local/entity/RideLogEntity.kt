package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ride_logs", indices = [Index("vehicleId"), Index("date")])
data class RideLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val date: Long,              // epoch millis
    val startOdometer: Int,
    val endOdometer: Int,
    val kmDriven: Int,           // endOdometer - startOdometer
    val tripName: String? = null,
    val notes: String? = null,
    val createdAt: Long
)
