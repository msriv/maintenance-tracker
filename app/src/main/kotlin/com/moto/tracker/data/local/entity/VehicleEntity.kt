package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nickname: String,
    val make: String,
    val model: String,
    val year: Int,
    val type: String,           // VehicleType enum name
    val vin: String? = null,
    val licensePlate: String? = null,
    val currentOdometer: Int,
    val fuelType: String,       // FuelType enum name
    val color: String? = null,
    val purchaseDate: Long? = null,
    val imageUri: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
