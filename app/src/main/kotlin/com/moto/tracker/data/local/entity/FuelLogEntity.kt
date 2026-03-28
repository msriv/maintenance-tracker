package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "fuel_logs",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId"), Index("fillDate")]
)
data class FuelLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val fillDate: Long,
    val odometer: Int,
    val liters: Double,
    val pricePerLiter: Double,
    val totalCost: Double,
    val fuelType: String,
    val stationName: String? = null,
    val isTankFull: Boolean = true,
    val kmPerLiter: Double? = null,     // null until computed after next full fill-up
    val notes: String? = null,
    val createdAt: Long
)
