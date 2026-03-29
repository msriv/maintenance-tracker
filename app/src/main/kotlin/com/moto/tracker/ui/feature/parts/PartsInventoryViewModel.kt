package com.moto.tracker.ui.feature.parts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.moto.tracker.PartsInventoryDestination
import com.moto.tracker.domain.model.PartsInventory
import com.moto.tracker.domain.usecase.parts.DeletePartUseCase
import com.moto.tracker.domain.usecase.parts.GetPartsInventoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PartsInventoryUiState(
    val parts: List<PartsInventory> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class PartsInventoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPartsInventory: GetPartsInventoryUseCase,
    private val deletePart: DeletePartUseCase
) : ViewModel() {

    private val vehicleId: Long = savedStateHandle.toRoute<PartsInventoryDestination>().vehicleId

    private val _uiState = MutableStateFlow(PartsInventoryUiState())
    val uiState: StateFlow<PartsInventoryUiState> = _uiState

    init {
        getPartsInventory(vehicleId)
            .onEach { parts ->
                _uiState.update { it.copy(parts = parts, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun deletePart(part: PartsInventory) {
        viewModelScope.launch {
            deletePart.invoke(part)
        }
    }
}
