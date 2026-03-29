package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "seasonal_checklists")
data class SeasonalChecklistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long? = null,  // null = global/template
    val name: String,
    val season: String,           // Season enum name
    val tasksJson: String,        // JSON array of task names
    val isTemplate: Boolean = false,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long
)
