package com.moto.tracker.domain.usecase.servicecenter

import com.moto.tracker.domain.repository.ServiceCenterRepository
import javax.inject.Inject

class SetServiceCenterFavoriteUseCase @Inject constructor(
    private val serviceCenterRepo: ServiceCenterRepository
) {
    suspend operator fun invoke(id: Long, isFavorite: Boolean) =
        serviceCenterRepo.setFavorite(id, isFavorite)
}
