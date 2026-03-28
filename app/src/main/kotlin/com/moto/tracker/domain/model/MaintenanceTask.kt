package com.moto.tracker.domain.model

data class MaintenanceTask(
    val id: Long = 0,
    val vehicleId: Long,
    val taskType: MaintenanceTaskType,
    val customName: String? = null,
    val thresholdDays: Int? = null,
    val thresholdKm: Int? = null,
    val lastPerformedDate: Long? = null,
    val lastPerformedOdometer: Int? = null,
    val nextDueDate: Long? = null,
    val nextDueOdometer: Int? = null,
    val isActive: Boolean = true,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    val displayName: String
        get() = if (taskType == MaintenanceTaskType.CUSTOM) customName ?: "Custom" else taskType.displayName

    fun duenessStatus(currentOdometer: Int): DuenessStatus {
        val now = System.currentTimeMillis()
        val warningWindow = 7L * 24 * 60 * 60 * 1000  // 7 days
        val kmWarningBuffer = 500

        val overdueByDate = nextDueDate != null && nextDueDate < now
        val overdueByKm = nextDueOdometer != null && nextDueOdometer <= currentOdometer

        if (overdueByDate || overdueByKm) return DuenessStatus.OVERDUE

        val dueSoonByDate = nextDueDate != null && nextDueDate < now + warningWindow
        val dueSoonByKm = nextDueOdometer != null && nextDueOdometer <= currentOdometer + kmWarningBuffer

        if (dueSoonByDate || dueSoonByKm) return DuenessStatus.DUE_SOON

        return DuenessStatus.OK
    }
}

enum class MaintenanceTaskType(val displayName: String) {
    ENGINE_OIL("Engine Oil Change"),
    AIR_FILTER("Air Filter Change"),
    BRAKE_OIL("Brake Oil Change"),
    CHAIN_LUBE("Chain Lubrication"),
    CHAIN_ADJUSTMENT("Chain Adjustment"),
    TIRE_ROTATION("Tyre Rotation"),
    TIRE_REPLACEMENT("Tyre Replacement"),
    BRAKE_PAD("Brake Pad Replacement"),
    SPARK_PLUG("Spark Plug Change"),
    COOLANT("Coolant Change"),
    TRANSMISSION_OIL("Transmission Oil Change"),
    BATTERY("Battery Check/Replacement"),
    CUSTOM("Custom")
}

enum class DuenessStatus {
    OK, DUE_SOON, OVERDUE
}

data class NextDueResult(
    val nextDueDate: Long?,
    val nextDueOdometer: Int?,
    val isDueByDate: Boolean,
    val isDueByKm: Boolean
) {
    val isOverdue: Boolean get() = isDueByDate || isDueByKm
}
