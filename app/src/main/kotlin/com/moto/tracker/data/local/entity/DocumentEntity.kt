package com.moto.tracker.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "documents",
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("vehicleId")]
)
data class DocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val type: String,           // DocumentType enum name
    val title: String,
    val fileUri: String? = null,
    val thumbnailUri: String? = null,
    val expiryDate: Long? = null,
    val issueDate: Long? = null,
    val notes: String? = null,
    val amount: Double? = null,
    val createdAt: Long,
    val updatedAt: Long
)
