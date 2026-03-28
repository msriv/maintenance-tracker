package com.moto.tracker.domain.usecase.maintenance

import com.moto.tracker.domain.model.MaintenanceTask
import com.moto.tracker.domain.repository.MaintenanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMaintenanceTasksUseCase @Inject constructor(
    private val repository: MaintenanceRepository
) {
    operator fun invoke(vehicleId: Long): Flow<List<MaintenanceTask>> =
        repository.observeTasksByVehicle(vehicleId)
}
