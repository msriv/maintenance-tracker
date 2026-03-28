package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.FuelLogEntity
import com.moto.tracker.domain.model.FuelLog

fun FuelLogEntity.toDomain(): FuelLog = FuelLog(
    id = id,
    vehicleId = vehicleId,
    fillDate = fillDate,
    odometer = odometer,
    liters = liters,
    pricePerLiter = pricePerLiter,
    totalCost = totalCost,
    fuelType = fuelType,
    stationName = stationName,
    isTankFull = isTankFull,
    kmPerLiter = kmPerLiter,
    notes = notes,
    createdAt = createdAt
)

fun FuelLog.toEntity(): FuelLogEntity = FuelLogEntity(
    id = id,
    vehicleId = vehicleId,
    fillDate = fillDate,
    odometer = odometer,
    liters = liters,
    pricePerLiter = pricePerLiter,
    totalCost = totalCost,
    fuelType = fuelType,
    stationName = stationName,
    isTankFull = isTankFull,
    kmPerLiter = kmPerLiter,
    notes = notes,
    createdAt = createdAt
)
