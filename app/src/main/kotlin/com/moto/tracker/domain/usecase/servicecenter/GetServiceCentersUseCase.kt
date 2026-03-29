package com.moto.tracker.domain.usecase.servicecenter

import com.moto.tracker.domain.model.ServiceCenter
import com.moto.tracker.domain.repository.ServiceCenterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServiceCentersUseCase @Inject constructor(
    private val serviceCenterRepo: ServiceCenterRepository
) {
    operator fun invoke(): Flow<List<ServiceCenter>> = serviceCenterRepo.observeAll()
}
