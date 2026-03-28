package com.moto.tracker.ui.feature.fuel.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.FuelLog
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.repository.FuelRepository
import com.moto.tracker.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FuelLogListUiState(
    val vehicle: Vehicle? = null,
    val fuelLogs: List<FuelLog> = emptyList(),
    val averageKmPerLiter: Double? = null,
    val totalFuelCost: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class FuelLogListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fuelRepository: FuelRepository,
    private val vehicleRepository: VehicleRepository
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])

    val uiState: StateFlow<FuelLogListUiState> = combine(
        vehicleRepository.observeById(vehicleId),
        fuelRepository.observeByVehicle(vehicleId)
    ) { vehicle, logs ->
        val avg = logs.mapNotNull { it.kmPerLiter }.average().let { if (it.isNaN()) null else it }
        FuelLogListUiState(
            vehicle = vehicle,
            fuelLogs = logs,
            averageKmPerLiter = avg,
            totalFuelCost = logs.sumOf { it.totalCost },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FuelLogListUiState(isLoading = true)
    )

    fun onDeleteLog(log: FuelLog) {
        viewModelScope.launch { fuelRepository.delete(log) }
    }
}
