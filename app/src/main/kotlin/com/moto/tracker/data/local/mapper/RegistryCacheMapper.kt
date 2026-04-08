package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.BestPracticeEntity
import com.moto.tracker.data.local.entity.RegistryMakeEntity
import com.moto.tracker.data.local.entity.RegistryModelEntity
import com.moto.tracker.data.local.entity.RegistryScheduleEntity
import com.moto.tracker.domain.model.BestPractice
import com.moto.tracker.domain.model.RegistryMake
import com.moto.tracker.domain.model.RegistryModel
import com.moto.tracker.domain.model.RegistrySchedule
import com.moto.tracker.domain.model.VehicleType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val registryJson = Json { ignoreUnknownKeys = true }

fun RegistryMakeEntity.toDomain(): RegistryMake = RegistryMake(
    id = id,
    name = name,
    country = country,
    logoUrl = logoUrl,
    updatedAt = updatedAt
)

fun RegistryMake.toEntity(): RegistryMakeEntity = RegistryMakeEntity(
    id = id,
    name = name,
    country = country,
    logoUrl = logoUrl,
    updatedAt = updatedAt
)

fun RegistryModelEntity.toDomain(): RegistryModel = RegistryModel(
    id = id,
    make = make,
    model = model,
    yearFrom = yearFrom,
    yearTo = yearTo,
    variants = registryJson.decodeFromString<List<String>>(variantsJson),
    vehicleType = VehicleType.valueOf(vehicleType),
    displacementCc = displacementCc,
    imageUrl = imageUrl,
    updatedAt = updatedAt
)

fun RegistryModel.toEntity(): RegistryModelEntity = RegistryModelEntity(
    id = id,
    make = make,
    model = model,
    yearFrom = yearFrom,
    yearTo = yearTo,
    variantsJson = registryJson.encodeToString<List<String>>(variants),
    vehicleType = vehicleType.name,
    displacementCc = displacementCc,
    imageUrl = imageUrl,
    updatedAt = updatedAt
)

fun RegistryScheduleEntity.toDomain(): RegistrySchedule = RegistrySchedule(
    id = id,
    make = make,
    model = model,
    yearFrom = yearFrom,
    yearTo = yearTo,
    taskType = taskType,
    taskLabel = taskLabel,
    intervalDays = intervalDays,
    intervalKm = intervalKm,
    notes = notes,
    source = source,
    updatedAt = updatedAt
)

fun RegistrySchedule.toEntity(): RegistryScheduleEntity = RegistryScheduleEntity(
    id = id,
    make = make,
    model = model,
    yearFrom = yearFrom,
    yearTo = yearTo,
    taskType = taskType,
    taskLabel = taskLabel,
    intervalDays = intervalDays,
    intervalKm = intervalKm,
    notes = notes,
    source = source,
    updatedAt = updatedAt
)

fun BestPracticeEntity.toDomain(): BestPractice = BestPractice(
    id = id,
    category = category,
    taskType = taskType,
    title = title,
    description = description,
    applicableTo = registryJson.decodeFromString<List<String>>(applicableToJson),
    isTemplate = isTemplate,
    templateIntervalDays = templateIntervalDays,
    templateIntervalKm = templateIntervalKm,
    updatedAt = updatedAt
)

fun BestPractice.toEntity(): BestPracticeEntity = BestPracticeEntity(
    id = id,
    category = category,
    taskType = taskType,
    title = title,
    description = description,
    applicableToJson = registryJson.encodeToString<List<String>>(applicableTo),
    isTemplate = isTemplate,
    templateIntervalDays = templateIntervalDays,
    templateIntervalKm = templateIntervalKm,
    updatedAt = updatedAt
)
