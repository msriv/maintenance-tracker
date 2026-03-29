package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.RecallAlertEntity
import com.moto.tracker.domain.model.RecallAlert
import com.moto.tracker.domain.model.RecallSeverity

fun RecallAlertEntity.toDomain(): RecallAlert = RecallAlert(
    id = id, vehicleId = vehicleId, campaignId = campaignId,
    make = make, model = model, yearFrom = yearFrom, yearTo = yearTo,
    componentName = componentName, consequence = consequence, remedy = remedy,
    description = description,
    severity = RecallSeverity.valueOf(severity),
    issuedDate = issuedDate, nhtsaUrl = nhtsaUrl,
    isAcknowledged = isAcknowledged, createdAt = createdAt, updatedAt = updatedAt
)

fun RecallAlert.toEntity(): RecallAlertEntity = RecallAlertEntity(
    id = id, vehicleId = vehicleId, campaignId = campaignId,
    make = make, model = model, yearFrom = yearFrom, yearTo = yearTo,
    componentName = componentName, consequence = consequence, remedy = remedy,
    description = description, severity = severity.name,
    issuedDate = issuedDate, nhtsaUrl = nhtsaUrl,
    isAcknowledged = isAcknowledged, createdAt = createdAt, updatedAt = updatedAt
)
