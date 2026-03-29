package com.moto.tracker.domain.usecase.recall

import com.moto.tracker.domain.repository.RecallRepository
import javax.inject.Inject

class AcknowledgeRecallUseCase @Inject constructor(
    private val recallRepo: RecallRepository
) {
    suspend operator fun invoke(recallId: Long) = recallRepo.acknowledge(recallId)
}
