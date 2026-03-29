package com.moto.tracker.domain.model

data class PartsInventory(
    val id: Long = 0,
    val vehicleId: Long,
    val partName: String,
    val partNumber: String? = null,
    val quantity: Int,
    val minQuantity: Int,
    val unitCost: Double? = null,
    val purchaseDate: Long? = null,
    val notes: String? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    val isLowStock: Boolean get() = quantity < minQuantity
    val totalValue: Double? get() = unitCost?.times(quantity)
}
