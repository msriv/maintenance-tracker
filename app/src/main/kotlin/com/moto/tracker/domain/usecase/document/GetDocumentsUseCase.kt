package com.moto.tracker.domain.usecase.document

import com.moto.tracker.domain.model.Document
import com.moto.tracker.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDocumentsUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    operator fun invoke(vehicleId: Long): Flow<List<Document>> =
        repository.observeByVehicle(vehicleId)
}
