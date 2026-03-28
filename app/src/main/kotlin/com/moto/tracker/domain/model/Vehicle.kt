package com.moto.tracker.domain.model

data class Vehicle(
    val id: Long = 0,
    val nickname: String,
    val make: String,
    val model: String,
    val year: Int,
    val type: VehicleType,
    val vin: String? = null,
    val licensePlate: String? = null,
    val currentOdometer: Int,
    val fuelType: FuelType,
    val color: String? = null,
    val purchaseDate: Long? = null,
    val imageUri: String? = null,
    val createdAt: Long,
    val updatedAt: Long
) {
    val displayName: String get() = if (nickname.isNotBlank()) nickname else "$year $make $model"
}

enum class VehicleType(val displayName: String) {
    BIKE("Motorcycle"),
    SCOOTER("Scooter"),
    CAR("Car"),
    TRUCK("Truck"),
    OTHER("Other")
}

enum class FuelType(val displayName: String) {
    PETROL("Petrol"),
    DIESEL("Diesel"),
    CNG("CNG"),
    ELECTRIC("Electric"),
    HYBRID("Hybrid")
}
