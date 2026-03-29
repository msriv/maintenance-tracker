package com.moto.tracker.domain.usecase.parts

import com.moto.tracker.domain.model.PartsInventory
import com.moto.tracker.domain.repository.PartsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLowStockPartsUseCase @Inject constructor(
    private val partsRepo: PartsRepository
) {
    operator fun invoke(vehicleId: Long): Flow<List<PartsInventory>> =
        partsRepo.observeLowStock(vehicleId)
}
