package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.PartsInventoryEntity
import com.moto.tracker.domain.model.PartsInventory

fun PartsInventoryEntity.toDomain(): PartsInventory = PartsInventory(
    id = id,
    vehicleId = vehicleId,
    partName = partName,
    partNumber = partNumber,
    quantity = quantity,
    minQuantity = minQuantity,
    unitCost = unitCost,
    purchaseDate = purchaseDate,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun PartsInventory.toEntity(): PartsInventoryEntity = PartsInventoryEntity(
    id = id,
    vehicleId = vehicleId,
    partName = partName,
    partNumber = partNumber,
    quantity = quantity,
    minQuantity = minQuantity,
    unitCost = unitCost,
    purchaseDate = purchaseDate,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)
