package com.moto.tracker.ui.feature.fuel.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.FuelLog
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.repository.VehicleRepository
import com.moto.tracker.domain.usecase.fuel.AddFuelLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddFuelLogUiState(
    val vehicle: Vehicle? = null,
    val fillDate: Long = System.currentTimeMillis(),
    val odometer: String = "",
    val liters: String = "",
    val pricePerLiter: String = "",
    val stationName: String = "",
    val isTankFull: Boolean = true,
    val notes: String = "",
    val isLoading: Boolean = false,
    val odometerError: String? = null,
    val litersError: String? = null,
    val priceError: String? = null
) {
    val totalCost: Double
        get() {
            val l = liters.toDoubleOrNull() ?: return 0.0
            val p = pricePerLiter.toDoubleOrNull() ?: return 0.0
            return l * p
        }
}

sealed class AddFuelLogEvent {
    object NavigateBack : AddFuelLogEvent()
}

@HiltViewModel
class AddFuelLogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vehicleRepository: VehicleRepository,
    private val addFuelLogUseCase: AddFuelLogUseCase
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])

    private val _uiState = MutableStateFlow(AddFuelLogUiState())
    val uiState: StateFlow<AddFuelLogUiState> = _uiState.asStateFlow()

    private val _events = Channel<AddFuelLogEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val vehicle = vehicleRepository.getById(vehicleId)
            _uiState.update { it.copy(vehicle = vehicle, odometer = vehicle?.currentOdometer?.toString() ?: "") }
        }
    }

    fun onFillDateChange(millis: Long) = _uiState.update { it.copy(fillDate = millis) }
    fun onOdometerChange(value: String) = _uiState.update { it.copy(odometer = value, odometerError = null) }
    fun onLitersChange(value: String) = _uiState.update { it.copy(liters = value, litersError = null) }
    fun onPricePerLiterChange(value: String) = _uiState.update { it.copy(pricePerLiter = value, priceError = null) }
    fun onStationNameChange(value: String) = _uiState.update { it.copy(stationName = value) }
    fun onIsTankFullChange(value: Boolean) = _uiState.update { it.copy(isTankFull = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun onSave() {
        val state = _uiState.value
        var hasError = false

        val odometer = state.odometer.toIntOrNull()
        if (odometer == null || odometer < 0) {
            _uiState.update { it.copy(odometerError = "Enter a valid odometer reading") }
            hasError = true
        }

        val liters = state.liters.toDoubleOrNull()
        if (liters == null || liters <= 0) {
            _uiState.update { it.copy(litersError = "Enter liters filled") }
            hasError = true
        }

        val price = state.pricePerLiter.toDoubleOrNull()
        if (price == null || price <= 0) {
            _uiState.update { it.copy(priceError = "Enter price per liter") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val now = System.currentTimeMillis()
            val log = FuelLog(
                vehicleId = vehicleId,
                fillDate = state.fillDate,
                odometer = odometer!!,
                liters = liters!!,
                pricePerLiter = price!!,
                totalCost = liters * price,
                fuelType = state.vehicle?.fuelType?.name ?: "PETROL",
                stationName = state.stationName.ifBlank { null },
                isTankFull = state.isTankFull,
                notes = state.notes.ifBlank { null },
                createdAt = now
            )

            addFuelLogUseCase(log)
                .onSuccess { _events.send(AddFuelLogEvent.NavigateBack) }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }
}
