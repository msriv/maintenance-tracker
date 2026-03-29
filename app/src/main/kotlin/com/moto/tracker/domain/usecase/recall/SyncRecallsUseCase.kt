package com.moto.tracker.domain.usecase.recall

import com.moto.tracker.domain.repository.RecallRepository
import javax.inject.Inject

class SyncRecallsUseCase @Inject constructor(
    private val recallRepo: RecallRepository
) {
    suspend operator fun invoke(vehicleId: Long, make: String, model: String, year: Int): Result<Int> =
        recallRepo.fetchAndStoreRecalls(vehicleId, make, model, year)
}
