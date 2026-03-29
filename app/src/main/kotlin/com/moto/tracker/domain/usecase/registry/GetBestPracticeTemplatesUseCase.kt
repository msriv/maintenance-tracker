package com.moto.tracker.domain.usecase.registry

import com.moto.tracker.domain.model.BestPractice
import com.moto.tracker.domain.repository.RegistryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBestPracticeTemplatesUseCase @Inject constructor(
    private val registryRepo: RegistryRepository
) {
    operator fun invoke(): Flow<List<BestPractice>> = registryRepo.observeBestPracticeTemplates()
}
