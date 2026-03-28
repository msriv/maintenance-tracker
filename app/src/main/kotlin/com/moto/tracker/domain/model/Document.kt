package com.moto.tracker.domain.model

data class Document(
    val id: Long = 0,
    val vehicleId: Long,
    val type: DocumentType,
    val title: String,
    val fileUri: String? = null,
    val thumbnailUri: String? = null,
    val expiryDate: Long? = null,
    val issueDate: Long? = null,
    val notes: String? = null,
    val amount: Double? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    val expiryStatus: ExpiryStatus
        get() {
            val now = System.currentTimeMillis()
            val thirtyDays = 30L * 24 * 60 * 60 * 1000
            return when {
                expiryDate == null -> ExpiryStatus.NO_EXPIRY
                expiryDate < now -> ExpiryStatus.EXPIRED
                expiryDate < now + thirtyDays -> ExpiryStatus.EXPIRING_SOON
                else -> ExpiryStatus.VALID
            }
        }
}

enum class DocumentType(val displayName: String) {
    RC("Registration (RC)"),
    INSURANCE("Insurance"),
    LICENSE("License"),
    FUEL_BILL("Fuel Bill"),
    SERVICE_BILL("Service Bill"),
    RECEIPT("Receipt"),
    PUC("PUC Certificate"),
    WARRANTY("Warranty"),
    OTHER("Other")
}

enum class ExpiryStatus {
    VALID, EXPIRING_SOON, EXPIRED, NO_EXPIRY
}
