package com.moto.tracker.domain.model

data class RegistryMake(
    val id: String,
    val name: String,
    val country: String? = null,
    val logoUrl: String? = null,
    val updatedAt: Long
)

data class RegistryModel(
    val id: String,
    val make: String,
    val model: String,
    val yearFrom: Int,
    val yearTo: Int? = null,
    val variants: List<String>,
    val vehicleType: VehicleType,
    val displacementCc: Int? = null,
    val updatedAt: Long
)

data class RegistrySchedule(
    val id: String,
    val make: String,
    val model: String,
    val yearFrom: Int,
    val yearTo: Int? = null,
    val taskType: String,
    val taskLabel: String,
    val intervalDays: Int? = null,
    val intervalKm: Int? = null,
    val notes: String? = null,
    val source: String,
    val updatedAt: Long
)

data class BestPractice(
    val id: String,
    val category: String,
    val taskType: String,
    val title: String,
    val description: String,
    val applicableTo: List<String>,
    val isTemplate: Boolean,
    val templateIntervalDays: Int? = null,
    val templateIntervalKm: Int? = null,
    val updatedAt: Long
)

data class VinDecodeResult(
    val make: String? = null,
    val model: String? = null,
    val year: Int? = null,
    val trim: String? = null,
    val engineType: String? = null,
    val isSuccess: Boolean,
    val errorMessage: String? = null
)

data class BackupStatus(
    val lastBackupAt: Long? = null,
    val isBackingUp: Boolean = false,
    val isRestoring: Boolean = false,
    val errorMessage: String? = null
)
