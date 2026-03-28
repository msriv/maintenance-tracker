package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.ManufacturerScheduleEntity
import com.moto.tracker.domain.model.ManufacturerSchedule

fun ManufacturerScheduleEntity.toDomain(): ManufacturerSchedule = ManufacturerSchedule(
    id = id,
    make = make,
    model = model,
    yearFrom = yearFrom,
    yearTo = yearTo,
    taskType = taskType,
    taskLabel = taskLabel,
    intervalDays = intervalDays,
    intervalKm = intervalKm,
    notes = notes
)
