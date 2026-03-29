package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.RideLogEntity
import com.moto.tracker.domain.model.RideLog

fun RideLogEntity.toDomain(): RideLog = RideLog(
    id = id,
    vehicleId = vehicleId,
    date = date,
    startOdometer = startOdometer,
    endOdometer = endOdometer,
    kmDriven = kmDriven,
    tripName = tripName,
    notes = notes,
    createdAt = createdAt
)

fun RideLog.toEntity(): RideLogEntity = RideLogEntity(
    id = id,
    vehicleId = vehicleId,
    date = date,
    startOdometer = startOdometer,
    endOdometer = endOdometer,
    kmDriven = kmDriven,
    tripName = tripName,
    notes = notes,
    createdAt = createdAt
)
