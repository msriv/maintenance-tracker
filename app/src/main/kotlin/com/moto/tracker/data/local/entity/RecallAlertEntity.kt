package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "recall_alerts", indices = [Index("vehicleId"), Index("campaignId")])
data class RecallAlertEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val campaignId: String,      // NHTSA campaign number
    val make: String,
    val model: String,
    val yearFrom: Int,
    val yearTo: Int,
    val componentName: String,
    val consequence: String,
    val remedy: String,
    val description: String,
    val severity: String,        // RecallSeverity enum name
    val issuedDate: String,      // ISO date string e.g. "2023-05-15"
    val nhtsaUrl: String,
    val isAcknowledged: Boolean = false,
    val createdAt: Long,
    val updatedAt: Long
)
