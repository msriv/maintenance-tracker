package com.moto.tracker.ui.feature.parts.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.moto.tracker.AddEditPartDestination
import com.moto.tracker.domain.model.PartsInventory
import com.moto.tracker.domain.usecase.parts.GetPartsInventoryUseCase
import com.moto.tracker.domain.usecase.parts.UpsertPartUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditPartUiState(
    val partName: String = "",
    val partNumber: String = "",
    val quantity: String = "1",
    val minQuantity: String = "1",
    val unitCost: String = "",
    val purchaseDate: Long? = null,
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AddEditPartViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPartsInventory: GetPartsInventoryUseCase,
    private val upsertPart: UpsertPartUseCase
) : ViewModel() {

    private val dest = savedStateHandle.toRoute<AddEditPartDestination>()
    private val vehicleId: Long = dest.vehicleId
    private val partId: Long? = dest.partId.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(AddEditPartUiState())
    val uiState: StateFlow<AddEditPartUiState> = _uiState

    val isEditMode: Boolean get() = partId != null

    init {
        if (partId != null) {
            viewModelScope.launch {
                val parts = getPartsInventory(vehicleId).first()
                val existing = parts.find { it.id == partId }
                existing?.let { part ->
                    _uiState.update {
                        it.copy(
                            partName = part.partName,
                            partNumber = part.partNumber ?: "",
                            quantity = part.quantity.toString(),
                            minQuantity = part.minQuantity.toString(),
                            unitCost = part.unitCost?.toString() ?: "",
                            purchaseDate = part.purchaseDate,
                            notes = part.notes ?: ""
                        )
                    }
                }
            }
        }
    }

    fun updatePartName(value: String) = _uiState.update { it.copy(partName = value) }
    fun updatePartNumber(value: String) = _uiState.update { it.copy(partNumber = value) }
    fun updateQuantity(value: String) = _uiState.update { it.copy(quantity = value) }
    fun updateMinQuantity(value: String) = _uiState.update { it.copy(minQuantity = value) }
    fun updateUnitCost(value: String) = _uiState.update { it.copy(unitCost = value) }
    fun updatePurchaseDate(value: Long?) = _uiState.update { it.copy(purchaseDate = value) }
    fun updateNotes(value: String) = _uiState.update { it.copy(notes = value) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.partName.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Part name is required") }
            return
        }
        val qty = state.quantity.toIntOrNull()
        if (qty == null || qty < 0) {
            _uiState.update { it.copy(errorMessage = "Enter a valid quantity") }
            return
        }
        val minQty = state.minQuantity.toIntOrNull()
        if (minQty == null || minQty < 0) {
            _uiState.update { it.copy(errorMessage = "Enter a valid minimum quantity") }
            return
        }
        val cost = if (state.unitCost.isBlank()) null else state.unitCost.toDoubleOrNull()

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val now = System.currentTimeMillis()
            val part = PartsInventory(
                id = partId ?: 0L,
                vehicleId = vehicleId,
                partName = state.partName.trim(),
                partNumber = state.partNumber.trim().ifBlank { null },
                quantity = qty,
                minQuantity = minQty,
                unitCost = cost,
                purchaseDate = state.purchaseDate,
                notes = state.notes.trim().ifBlank { null },
                createdAt = now,
                updatedAt = now
            )
            runCatching { upsertPart(part) }
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    onSaved()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save part") }
                }
        }
    }
}
