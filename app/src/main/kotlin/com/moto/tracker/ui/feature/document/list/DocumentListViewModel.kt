package com.moto.tracker.ui.feature.document.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.Document
import com.moto.tracker.domain.model.DocumentType
import com.moto.tracker.domain.repository.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DocumentListUiState(
    val groupedDocuments: Map<DocumentType, List<Document>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class DocumentListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val documentRepository: DocumentRepository
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])

    val uiState: StateFlow<DocumentListUiState> = documentRepository.observeByVehicle(vehicleId)
        .map { documents ->
            DocumentListUiState(
                groupedDocuments = documents.groupBy { it.type },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DocumentListUiState(isLoading = true)
        )

    fun onDeleteDocument(document: Document) {
        viewModelScope.launch {
            documentRepository.delete(document)
        }
    }
}
