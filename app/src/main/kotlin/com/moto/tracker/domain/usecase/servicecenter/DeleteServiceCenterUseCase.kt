package com.moto.tracker.domain.usecase.servicecenter

import com.moto.tracker.domain.model.ServiceCenter
import com.moto.tracker.domain.repository.ServiceCenterRepository
import javax.inject.Inject

class DeleteServiceCenterUseCase @Inject constructor(
    private val serviceCenterRepo: ServiceCenterRepository
) {
    suspend operator fun invoke(center: ServiceCenter) = serviceCenterRepo.delete(center)
}
