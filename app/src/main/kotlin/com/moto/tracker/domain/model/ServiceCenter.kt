package com.moto.tracker.domain.model

data class ServiceCenter(
    val id: Long = 0,
    val name: String,
    val address: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val rating: Float? = null,
    val notes: String? = null,
    val isFavorite: Boolean = false,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val createdAt: Long,
    val updatedAt: Long
)
