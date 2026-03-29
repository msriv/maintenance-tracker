package com.moto.tracker.domain.usecase.seasonal

import com.moto.tracker.domain.model.SeasonalChecklist
import com.moto.tracker.domain.repository.SeasonalChecklistRepository
import javax.inject.Inject

class DeleteSeasonalChecklistUseCase @Inject constructor(
    private val checklistRepo: SeasonalChecklistRepository
) {
    suspend operator fun invoke(checklist: SeasonalChecklist) = checklistRepo.delete(checklist)
}
