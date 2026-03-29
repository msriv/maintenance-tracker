package com.moto.tracker.domain.usecase.registry

import com.moto.tracker.domain.repository.RegistryRepository
import javax.inject.Inject

class SyncRegistryUseCase @Inject constructor(
    private val registryRepo: RegistryRepository
) {
    suspend operator fun invoke(): Result<Unit> = registryRepo.syncFromFirestore()
}
