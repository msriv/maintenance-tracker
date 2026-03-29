package com.moto.tracker.domain.model

enum class RecallSeverity(val displayName: String) {
    MINOR("Minor"),
    MODERATE("Moderate"),
    CRITICAL("Critical")
}

data class RecallAlert(
    val id: Long = 0,
    val vehicleId: Long,
    val campaignId: String,
    val make: String,
    val model: String,
    val yearFrom: Int,
    val yearTo: Int,
    val componentName: String,
    val consequence: String,
    val remedy: String,
    val description: String,
    val severity: RecallSeverity,
    val issuedDate: String,
    val nhtsaUrl: String,
    val isAcknowledged: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)
