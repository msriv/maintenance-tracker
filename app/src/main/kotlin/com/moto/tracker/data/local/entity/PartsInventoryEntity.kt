package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "parts_inventory", indices = [Index("vehicleId")])
data class PartsInventoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val partName: String,
    val partNumber: String? = null,
    val quantity: Int = 0,
    val minQuantity: Int = 1,   // alert when qty falls below this
    val unitCost: Double? = null,
    val purchaseDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
