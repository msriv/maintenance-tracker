package com.moto.tracker.domain.usecase.seasonal

import com.moto.tracker.domain.model.SeasonalChecklist
import com.moto.tracker.domain.repository.SeasonalChecklistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSeasonalChecklistsUseCase @Inject constructor(
    private val checklistRepo: SeasonalChecklistRepository
) {
    operator fun invoke(): Flow<List<SeasonalChecklist>> = checklistRepo.observeAll()
}
