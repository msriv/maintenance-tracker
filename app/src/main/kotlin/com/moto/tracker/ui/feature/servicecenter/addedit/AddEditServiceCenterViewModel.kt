package com.moto.tracker.ui.feature.servicecenter.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.moto.tracker.AddEditServiceCenterDestination
import com.moto.tracker.domain.model.ServiceCenter
import com.moto.tracker.domain.usecase.servicecenter.GetServiceCentersUseCase
import com.moto.tracker.domain.usecase.servicecenter.UpsertServiceCenterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditServiceCenterUiState(
    val name: String = "",
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val website: String = "",
    val rating: Float = 0f,
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddEditServiceCenterViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getServiceCenters: GetServiceCentersUseCase,
    private val upsertServiceCenter: UpsertServiceCenterUseCase
) : ViewModel() {

    private val centerId: Long? = savedStateHandle.toRoute<AddEditServiceCenterDestination>()
        .centerId.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(AddEditServiceCenterUiState())
    val uiState: StateFlow<AddEditServiceCenterUiState> = _uiState

    val isEditMode: Boolean get() = centerId != null

    init {
        if (centerId != null) {
            viewModelScope.launch {
                val centers = getServiceCenters().first()
                val existing = centers.find { it.id == centerId }
                existing?.let { center ->
                    _uiState.update {
                        it.copy(
                            name = center.name,
                            address = center.address ?: "",
                            phone = center.phone ?: "",
                            email = center.email ?: "",
                            website = center.website ?: "",
                            rating = center.rating ?: 0f,
                            notes = center.notes ?: ""
                        )
                    }
                }
            }
        }
    }

    fun updateName(value: String) = _uiState.update { it.copy(name = value) }
    fun updateAddress(value: String) = _uiState.update { it.copy(address = value) }
    fun updatePhone(value: String) = _uiState.update { it.copy(phone = value) }
    fun updateEmail(value: String) = _uiState.update { it.copy(email = value) }
    fun updateWebsite(value: String) = _uiState.update { it.copy(website = value) }
    fun updateRating(value: Float) = _uiState.update { it.copy(rating = value) }
    fun updateNotes(value: String) = _uiState.update { it.copy(notes = value) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Name is required") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val now = System.currentTimeMillis()
            val center = ServiceCenter(
                id = centerId ?: 0L,
                name = state.name.trim(),
                address = state.address.trim().ifBlank { null },
                phone = state.phone.trim().ifBlank { null },
                email = state.email.trim().ifBlank { null },
                website = state.website.trim().ifBlank { null },
                rating = if (state.rating > 0f) state.rating else null,
                notes = state.notes.trim().ifBlank { null },
                createdAt = now,
                updatedAt = now
            )
            runCatching { upsertServiceCenter(center) }
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    onSaved()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save") }
                }
        }
    }
}
