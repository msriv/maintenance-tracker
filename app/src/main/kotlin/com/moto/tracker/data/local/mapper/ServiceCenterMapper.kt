package com.moto.tracker.data.local.mapper

import com.moto.tracker.data.local.entity.ServiceCenterEntity
import com.moto.tracker.domain.model.ServiceCenter

fun ServiceCenterEntity.toDomain(): ServiceCenter = ServiceCenter(
    id = id,
    name = name,
    address = address,
    phone = phone,
    email = email,
    website = website,
    rating = rating,
    notes = notes,
    isFavorite = isFavorite,
    latitude = latitude,
    longitude = longitude,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ServiceCenter.toEntity(): ServiceCenterEntity = ServiceCenterEntity(
    id = id,
    name = name,
    address = address,
    phone = phone,
    email = email,
    website = website,
    rating = rating,
    notes = notes,
    isFavorite = isFavorite,
    latitude = latitude,
    longitude = longitude,
    createdAt = createdAt,
    updatedAt = updatedAt
)
