package com.moto.tracker.domain.usecase.seasonal

import com.moto.tracker.domain.model.SeasonalChecklist
import com.moto.tracker.domain.repository.SeasonalChecklistRepository
import javax.inject.Inject

class UpsertSeasonalChecklistUseCase @Inject constructor(
    private val checklistRepo: SeasonalChecklistRepository
) {
    suspend operator fun invoke(checklist: SeasonalChecklist): Long = checklistRepo.upsert(checklist)
}
