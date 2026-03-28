package com.moto.tracker.ui.feature.vehicle.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.usecase.vehicle.DeleteVehicleUseCase
import com.moto.tracker.domain.usecase.vehicle.GetVehiclesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VehicleListUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class VehicleListViewModel @Inject constructor(
    private val getVehicles: GetVehiclesUseCase,
    private val deleteVehicle: DeleteVehicleUseCase
) : ViewModel() {

    val uiState: StateFlow<VehicleListUiState> = getVehicles()
        .map { vehicles -> VehicleListUiState(vehicles = vehicles, isLoading = false) }
        .catch { e -> emit(VehicleListUiState(isLoading = false, errorMessage = e.message)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VehicleListUiState(isLoading = true)
        )

    fun onDeleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            deleteVehicle(vehicle)
        }
    }
}
