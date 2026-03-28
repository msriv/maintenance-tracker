package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.VehicleEntity
import com.moto.tracker.domain.model.FuelType
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.model.VehicleType

fun VehicleEntity.toDomain(): Vehicle = Vehicle(
    id = id,
    nickname = nickname,
    make = make,
    model = model,
    year = year,
    type = VehicleType.valueOf(type),
    vin = vin,
    licensePlate = licensePlate,
    currentOdometer = currentOdometer,
    fuelType = FuelType.valueOf(fuelType),
    color = color,
    purchaseDate = purchaseDate,
    imageUri = imageUri,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Vehicle.toEntity(): VehicleEntity = VehicleEntity(
    id = id,
    nickname = nickname,
    make = make,
    model = model,
    year = year,
    type = type.name,
    vin = vin,
    licensePlate = licensePlate,
    currentOdometer = currentOdometer,
    fuelType = fuelType.name,
    color = color,
    purchaseDate = purchaseDate,
    imageUri = imageUri,
    createdAt = createdAt,
    updatedAt = updatedAt
)
