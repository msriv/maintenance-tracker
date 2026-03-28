package com.moto.tracker.domain.usecase.document

import com.moto.tracker.domain.model.Document
import com.moto.tracker.domain.repository.DocumentRepository
import javax.inject.Inject

class AddDocumentUseCase @Inject constructor(
    private val repository: DocumentRepository
) {
    suspend operator fun invoke(document: Document): Result<Long> = runCatching {
        repository.insert(document)
    }
}
