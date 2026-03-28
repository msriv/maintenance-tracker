package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.KmLogEntity
import com.moto.tracker.data.local.entity.MaintenanceLogEntity
import com.moto.tracker.data.local.entity.MaintenanceTaskEntity
import com.moto.tracker.domain.model.KmLog
import com.moto.tracker.domain.model.MaintenanceLog
import com.moto.tracker.domain.model.MaintenanceTask
import com.moto.tracker.domain.model.MaintenanceTaskType

fun MaintenanceTaskEntity.toDomain(): MaintenanceTask = MaintenanceTask(
    id = id,
    vehicleId = vehicleId,
    taskType = MaintenanceTaskType.valueOf(taskType),
    customName = customName,
    thresholdDays = thresholdDays,
    thresholdKm = thresholdKm,
    lastPerformedDate = lastPerformedDate,
    lastPerformedOdometer = lastPerformedOdometer,
    nextDueDate = nextDueDate,
    nextDueOdometer = nextDueOdometer,
    isActive = isActive,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun MaintenanceTask.toEntity(): MaintenanceTaskEntity = MaintenanceTaskEntity(
    id = id,
    vehicleId = vehicleId,
    taskType = taskType.name,
    customName = customName,
    thresholdDays = thresholdDays,
    thresholdKm = thresholdKm,
    lastPerformedDate = lastPerformedDate,
    lastPerformedOdometer = lastPerformedOdometer,
    nextDueDate = nextDueDate,
    nextDueOdometer = nextDueOdometer,
    isActive = isActive,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun MaintenanceLogEntity.toDomain(): MaintenanceLog = MaintenanceLog(
    id = id,
    taskId = taskId,
    vehicleId = vehicleId,
    performedDate = performedDate,
    odometer = odometer,
    cost = cost,
    mechanicName = mechanicName,
    serviceCenterName = serviceCenterName,
    notes = notes,
    createdAt = createdAt
)

fun MaintenanceLog.toEntity(): MaintenanceLogEntity = MaintenanceLogEntity(
    id = id,
    taskId = taskId,
    vehicleId = vehicleId,
    performedDate = performedDate,
    odometer = odometer,
    cost = cost,
    mechanicName = mechanicName,
    serviceCenterName = serviceCenterName,
    notes = notes,
    createdAt = createdAt
)

fun KmLogEntity.toDomain(): KmLog = KmLog(
    id = id,
    vehicleId = vehicleId,
    logDate = logDate,
    odometer = odometer,
    kmDriven = kmDriven,
    notes = notes,
    createdAt = createdAt
)

fun KmLog.toEntity(): KmLogEntity = KmLogEntity(
    id = id,
    vehicleId = vehicleId,
    logDate = logDate,
    odometer = odometer,
    kmDriven = kmDriven,
    notes = notes,
    createdAt = createdAt
)
