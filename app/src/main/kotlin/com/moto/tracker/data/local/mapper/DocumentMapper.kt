package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.DocumentEntity
import com.moto.tracker.domain.model.Document
import com.moto.tracker.domain.model.DocumentType

fun DocumentEntity.toDomain(): Document = Document(
    id = id,
    vehicleId = vehicleId,
    type = DocumentType.valueOf(type),
    title = title,
    fileUri = fileUri,
    thumbnailUri = thumbnailUri,
    expiryDate = expiryDate,
    issueDate = issueDate,
    notes = notes,
    amount = amount,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Document.toEntity(): DocumentEntity = DocumentEntity(
    id = id,
    vehicleId = vehicleId,
    type = type.name,
    title = title,
    fileUri = fileUri,
    thumbnailUri = thumbnailUri,
    expiryDate = expiryDate,
    issueDate = issueDate,
    notes = notes,
    amount = amount,
    createdAt = createdAt,
    updatedAt = updatedAt
)
