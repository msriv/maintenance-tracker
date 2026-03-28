package com.moto.tracker.ui.feature.document.add

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.Document
import com.moto.tracker.domain.model.DocumentType
import com.moto.tracker.domain.usecase.document.AddDocumentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

data class AddDocumentUiState(
    val type: DocumentType = DocumentType.RC,
    val title: String = "",
    val expiryDate: Long? = null,
    val issueDate: Long? = null,
    val amount: String = "",
    val notes: String = "",
    val fileUri: String? = null,
    val isLoading: Boolean = false,
    val titleError: String? = null
)

sealed class AddDocumentEvent {
    object NavigateBack : AddDocumentEvent()
}

@HiltViewModel
class AddDocumentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val addDocument: AddDocumentUseCase
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])

    private val _uiState = MutableStateFlow(AddDocumentUiState())
    val uiState: StateFlow<AddDocumentUiState> = _uiState.asStateFlow()

    private val _events = Channel<AddDocumentEvent>()
    val events = _events.receiveAsFlow()

    fun onTypeChange(value: DocumentType) = _uiState.update { it.copy(type = value) }
    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value, titleError = null) }
    fun onExpiryDateChange(value: Long?) = _uiState.update { it.copy(expiryDate = value) }
    fun onIssueDateChange(value: Long?) = _uiState.update { it.copy(issueDate = value) }
    fun onAmountChange(value: String) = _uiState.update { it.copy(amount = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun onImageSelected(uri: Uri) {
        viewModelScope.launch {
            val savedPath = copyImageToInternalStorage(uri)
            _uiState.update { it.copy(fileUri = savedPath) }
        }
    }

    private fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val dir = File(context.filesDir, "documents").apply { mkdirs() }
            val destFile = File(dir, "${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                destFile.outputStream().use { output -> input.copyTo(output) }
            }
            destFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    fun onSave() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val now = System.currentTimeMillis()
            val doc = Document(
                vehicleId = vehicleId,
                type = state.type,
                title = state.title.trim(),
                fileUri = state.fileUri,
                expiryDate = state.expiryDate,
                issueDate = state.issueDate,
                amount = state.amount.toDoubleOrNull(),
                notes = state.notes.ifBlank { null },
                createdAt = now,
                updatedAt = now
            )
            addDocument(doc)
                .onSuccess { _events.send(AddDocumentEvent.NavigateBack) }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }
}
