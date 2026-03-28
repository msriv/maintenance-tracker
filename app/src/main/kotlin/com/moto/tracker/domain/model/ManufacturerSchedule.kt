package com.moto.tracker.domain.model

data class ManufacturerSchedule(
    val id: Long = 0,
    val make: String,
    val model: String,
    val yearFrom: Int,
    val yearTo: Int? = null,
    val taskType: String,
    val taskLabel: String,
    val intervalDays: Int? = null,
    val intervalKm: Int? = null,
    val notes: String? = null
)
