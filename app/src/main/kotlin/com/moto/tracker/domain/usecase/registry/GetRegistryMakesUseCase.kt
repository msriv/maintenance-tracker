package com.moto.tracker.domain.usecase.registry

import com.moto.tracker.domain.model.RegistryMake
import com.moto.tracker.domain.repository.RegistryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRegistryMakesUseCase @Inject constructor(
    private val registryRepo: RegistryRepository
) {
    operator fun invoke(): Flow<List<RegistryMake>> = registryRepo.observeMakes()
}
