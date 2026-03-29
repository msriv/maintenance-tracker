package com.moto.tracker.domain.usecase.registry

import com.moto.tracker.domain.model.RegistryModel
import com.moto.tracker.domain.repository.RegistryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRegistryModelsUseCase @Inject constructor(
    private val registryRepo: RegistryRepository
) {
    operator fun invoke(make: String): Flow<List<RegistryModel>> =
        registryRepo.observeModelsByMake(make)
}
