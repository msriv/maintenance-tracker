package com.moto.tracker.domain.usecase.ridelog

import com.moto.tracker.domain.model.RideLog
import com.moto.tracker.domain.repository.RideLogRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRideLogsUseCase @Inject constructor(
    private val rideLogRepo: RideLogRepository
) {
    operator fun invoke(vehicleId: Long): Flow<List<RideLog>> =
        rideLogRepo.observeByVehicle(vehicleId)
}
