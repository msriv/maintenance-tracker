package com.moto.tracker.ui.feature.document.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.Document
import com.moto.tracker.domain.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class DocumentDetailUiState(
    val document: Document? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class DocumentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val documentId: Long = requireNotNull(savedStateHandle["documentId"])

    val uiState: StateFlow<DocumentDetailUiState> = flow {
        emit(DocumentDetailUiState(document = documentRepository.getById(documentId), isLoading = false))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DocumentDetailUiState(isLoading = true)
    )
}
