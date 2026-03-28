package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "manufacturer_schedules",
    indices = [Index(value = ["make", "model"])]
)
data class ManufacturerScheduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val make: String,
    val model: String,
    val yearFrom: Int,
    val yearTo: Int? = null,
    val taskType: String,
    val taskLabel: String,
    val intervalDays: Int? = null,
    val intervalKm: Int? = null,
    val notes: String? = null
)
