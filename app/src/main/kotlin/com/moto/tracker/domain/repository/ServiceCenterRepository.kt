package com.moto.tracker.domain.repository

import com.moto.tracker.domain.model.ServiceCenter
import kotlinx.coroutines.flow.Flow

interface ServiceCenterRepository {
    fun observeAll(): Flow<List<ServiceCenter>>
    fun observeFavorites(): Flow<List<ServiceCenter>>
    suspend fun getById(id: Long): ServiceCenter?
    suspend fun upsert(center: ServiceCenter): Long
    suspend fun delete(center: ServiceCenter)
    suspend fun setFavorite(id: Long, isFavorite: Boolean)
}
