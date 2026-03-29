package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.SeasonalChecklistEntity
import com.moto.tracker.domain.model.Season
import com.moto.tracker.domain.model.SeasonalChecklist
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun SeasonalChecklistEntity.toDomain(): SeasonalChecklist = SeasonalChecklist(
    id = id,
    vehicleId = vehicleId,
    name = name,
    season = Season.valueOf(season),
    tasks = json.decodeFromString<List<String>>(tasksJson),
    isTemplate = isTemplate,
    isCompleted = isCompleted,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun SeasonalChecklist.toEntity(): SeasonalChecklistEntity = SeasonalChecklistEntity(
    id = id,
    vehicleId = vehicleId,
    name = name,
    season = season.name,
    tasksJson = json.encodeToString<List<String>>(tasks),
    isTemplate = isTemplate,
    isCompleted = isCompleted,
    completedAt = completedAt,
    createdAt = createdAt,
    updatedAt = updatedAt
)
