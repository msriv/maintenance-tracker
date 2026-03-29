package com.moto.tracker.domain.usecase.recall

import com.moto.tracker.domain.model.RecallAlert
import com.moto.tracker.domain.repository.RecallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecallAlertsUseCase @Inject constructor(
    private val recallRepo: RecallRepository
) {
    operator fun invoke(vehicleId: Long): Flow<List<RecallAlert>> =
        recallRepo.observeByVehicle(vehicleId)
}
