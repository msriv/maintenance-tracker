package com.moto.tracker.domain.usecase.ridelog

import com.moto.tracker.domain.model.RideLog
import com.moto.tracker.domain.repository.RideLogRepository
import javax.inject.Inject

class DeleteRideLogUseCase @Inject constructor(
    private val rideLogRepo: RideLogRepository
) {
    suspend operator fun invoke(ride: RideLog) = rideLogRepo.delete(ride)
}
