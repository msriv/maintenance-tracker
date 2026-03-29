package com.moto.tracker.domain.usecase.recall

import com.moto.tracker.domain.repository.RecallRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUnacknowledgedRecallCountUseCase @Inject constructor(
    private val recallRepo: RecallRepository
) {
    operator fun invoke(vehicleId: Long): Flow<Int> =
        recallRepo.observeUnacknowledgedCount(vehicleId)
}
