package com.moto.tracker.domain.usecase.parts

import com.moto.tracker.domain.model.PartsInventory
import com.moto.tracker.domain.repository.PartsRepository
import javax.inject.Inject

class UpsertPartUseCase @Inject constructor(
    private val partsRepo: PartsRepository
) {
    suspend operator fun invoke(part: PartsInventory): Long = partsRepo.upsert(part)
}
