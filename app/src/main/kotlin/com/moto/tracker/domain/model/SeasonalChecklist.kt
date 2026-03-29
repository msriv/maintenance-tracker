package com.moto.tracker.domain.model

enum class Season(val displayName: String) {
    SUMMER("Summer"),
    MONSOON("Monsoon / Wet"),
    WINTER("Winter"),
    ALL("All Season")
}

data class SeasonalChecklist(
    val id: Long = 0,
    val vehicleId: Long? = null,
    val name: String,
    val season: Season,
    val tasks: List<String>,     // parsed from tasksJson
    val isTemplate: Boolean,
    val isCompleted: Boolean,
    val completedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long
)
