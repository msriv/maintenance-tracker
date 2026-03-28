package com.moto.tracker.domain.usecase.vehicle

import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.repository.VehicleRepository
import javax.inject.Inject

class AddVehicleUseCase @Inject constructor(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(vehicle: Vehicle): Result<Long> = runCatching {
        repository.insert(vehicle)
    }
}
