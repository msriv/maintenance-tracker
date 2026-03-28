package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.ServiceAppointmentEntity
import com.moto.tracker.domain.model.AppointmentStatus
import com.moto.tracker.domain.model.ServiceAppointment

fun ServiceAppointmentEntity.toDomain(): ServiceAppointment = ServiceAppointment(
    id = id,
    vehicleId = vehicleId,
    appointmentDate = appointmentDate,
    title = title,
    serviceCenterName = serviceCenterName,
    serviceCenterAddress = serviceCenterAddress,
    serviceCenterPhone = serviceCenterPhone,
    taskType = taskType,
    estimatedCost = estimatedCost,
    actualCost = actualCost,
    status = AppointmentStatus.valueOf(status.uppercase()),
    reminderEnabled = reminderEnabled,
    reminderMinutesBefore = reminderMinutesBefore,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ServiceAppointment.toEntity(): ServiceAppointmentEntity = ServiceAppointmentEntity(
    id = id,
    vehicleId = vehicleId,
    appointmentDate = appointmentDate,
    title = title,
    serviceCenterName = serviceCenterName,
    serviceCenterAddress = serviceCenterAddress,
    serviceCenterPhone = serviceCenterPhone,
    taskType = taskType,
    estimatedCost = estimatedCost,
    actualCost = actualCost,
    status = status.name.lowercase(),
    reminderEnabled = reminderEnabled,
    reminderMinutesBefore = reminderMinutesBefore,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)
