package com.moto.tracker.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Cached registry data from Firestore (makes list)
@Entity(tableName = "registry_makes")
data class RegistryMakeEntity(
    @PrimaryKey val id: String,  // slugified make name e.g. "honda"
    val name: String,
    val country: String? = null,
    val logoUrl: String? = null,
    val updatedAt: Long
)

// Cached registry models from Firestore
@Entity(tableName = "registry_models", indices = [Index("make")])
data class RegistryModelEntity(
    @PrimaryKey val id: String,  // "{make}_{model}_{yearFrom}"
    val make: String,
    val model: String,
    val yearFrom: Int,
    val yearTo: Int? = null,
    val variantsJson: String,    // JSON array of variant strings
    val vehicleType: String,     // VehicleType enum name
    val displacementCc: Int? = null,
    @ColumnInfo(defaultValue = "") val imageUrl: String? = null,
    val updatedAt: Long
)

// Cached OEM schedules from Firestore
@Entity(tableName = "registry_schedules", indices = [Index(value = ["make", "model"])])
data class RegistryScheduleEntity(
    @PrimaryKey val id: String,  // "{make}_{model}_{yearFrom}_{taskType}"
    val make: String,
    val model: String,
    val yearFrom: Int,
    val yearTo: Int? = null,
    val taskType: String,
    val taskLabel: String,
    val intervalDays: Int? = null,
    val intervalKm: Int? = null,
    val notes: String? = null,
    val source: String,
    val updatedAt: Long
)

// Cached best practices from Firestore
@Entity(tableName = "best_practices")
data class BestPracticeEntity(
    @PrimaryKey val id: String,
    val category: String,        // "MANUFACTURER", "POST_SERVICE", "ROUTINE"
    val taskType: String,
    val title: String,
    val description: String,
    val applicableToJson: String, // JSON array of vehicle types
    val isTemplate: Boolean,
    val templateIntervalDays: Int? = null,
    val templateIntervalKm: Int? = null,
    val updatedAt: Long
)
