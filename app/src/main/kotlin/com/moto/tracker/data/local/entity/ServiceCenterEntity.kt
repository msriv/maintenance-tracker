package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_centers")
data class ServiceCenterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val address: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val rating: Float? = null,  // 1.0 - 5.0
    val notes: String? = null,
    val isFavorite: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdAt: Long,
    val updatedAt: Long
)
