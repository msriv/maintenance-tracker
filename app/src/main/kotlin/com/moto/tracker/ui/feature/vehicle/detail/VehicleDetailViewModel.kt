package com.moto.tracker.ui.feature.vehicle.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class VehicleDetailUiState(
    val vehicle: Vehicle? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class VehicleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])

    val uiState: StateFlow<VehicleDetailUiState> = vehicleRepository.observeById(vehicleId)
        .map { vehicle -> VehicleDetailUiState(vehicle = vehicle, isLoading = false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VehicleDetailUiState(isLoading = true)
        )
}
