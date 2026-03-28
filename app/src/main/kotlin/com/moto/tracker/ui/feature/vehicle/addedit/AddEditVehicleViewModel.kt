package com.moto.tracker.ui.feature.vehicle.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.FuelType
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.model.VehicleType
import com.moto.tracker.domain.repository.VehicleRepository
import com.moto.tracker.domain.usecase.vehicle.AddVehicleUseCase
import com.moto.tracker.domain.usecase.vehicle.UpdateVehicleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditVehicleUiState(
    val nickname: String = "",
    val make: String = "",
    val model: String = "",
    val year: String = "",
    val type: VehicleType = VehicleType.BIKE,
    val vin: String = "",
    val licensePlate: String = "",
    val currentOdometer: String = "0",
    val fuelType: FuelType = FuelType.PETROL,
    val color: String = "",
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
    // Validation errors
    val makeError: String? = null,
    val modelError: String? = null,
    val yearError: String? = null,
    val odometerError: String? = null
)

sealed class AddEditVehicleEvent {
    object NavigateBack : AddEditVehicleEvent()
}

@HiltViewModel
class AddEditVehicleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vehicleRepository: VehicleRepository,
    private val addVehicle: AddVehicleUseCase,
    private val updateVehicle: UpdateVehicleUseCase
) : ViewModel() {

    private val vehicleId: Long? = savedStateHandle.get<Long>("vehicleId")?.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(AddEditVehicleUiState(isEditMode = vehicleId != null))
    val uiState: StateFlow<AddEditVehicleUiState> = _uiState.asStateFlow()

    private val _events = Channel<AddEditVehicleEvent>()
    val events = _events.receiveAsFlow()

    init {
        vehicleId?.let { loadVehicle(it) }
    }

    private fun loadVehicle(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val vehicle = vehicleRepository.getById(id)
            if (vehicle != null) {
                _uiState.update { state ->
                    state.copy(
                        nickname = vehicle.nickname,
                        make = vehicle.make,
                        model = vehicle.model,
                        year = vehicle.year.toString(),
                        type = vehicle.type,
                        vin = vehicle.vin ?: "",
                        licensePlate = vehicle.licensePlate ?: "",
                        currentOdometer = vehicle.currentOdometer.toString(),
                        fuelType = vehicle.fuelType,
                        color = vehicle.color ?: "",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onNicknameChange(value: String) = _uiState.update { it.copy(nickname = value) }
    fun onMakeChange(value: String) = _uiState.update { it.copy(make = value, makeError = null) }
    fun onModelChange(value: String) = _uiState.update { it.copy(model = value, modelError = null) }
    fun onYearChange(value: String) = _uiState.update { it.copy(year = value, yearError = null) }
    fun onTypeChange(value: VehicleType) = _uiState.update { it.copy(type = value) }
    fun onVinChange(value: String) = _uiState.update { it.copy(vin = value) }
    fun onLicensePlateChange(value: String) = _uiState.update { it.copy(licensePlate = value) }
    fun onOdometerChange(value: String) = _uiState.update { it.copy(currentOdometer = value, odometerError = null) }
    fun onFuelTypeChange(value: FuelType) = _uiState.update { it.copy(fuelType = value) }
    fun onColorChange(value: String) = _uiState.update { it.copy(color = value) }

    fun onSave() {
        val state = _uiState.value
        if (!validate(state)) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val now = System.currentTimeMillis()

            val vehicle = Vehicle(
                id = vehicleId ?: 0,
                nickname = state.nickname.trim(),
                make = state.make.trim(),
                model = state.model.trim(),
                year = state.year.trim().toInt(),
                type = state.type,
                vin = state.vin.trim().ifBlank { null },
                licensePlate = state.licensePlate.trim().ifBlank { null },
                currentOdometer = state.currentOdometer.trim().toInt(),
                fuelType = state.fuelType,
                color = state.color.trim().ifBlank { null },
                createdAt = if (vehicleId == null) now else 0L,
                updatedAt = now
            )

            val result = if (vehicleId == null) addVehicle(vehicle) else updateVehicle(vehicle)
            result.onSuccess { _events.send(AddEditVehicleEvent.NavigateBack) }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    private fun validate(state: AddEditVehicleUiState): Boolean {
        var valid = true
        if (state.make.isBlank()) {
            _uiState.update { it.copy(makeError = "Make is required") }
            valid = false
        }
        if (state.model.isBlank()) {
            _uiState.update { it.copy(modelError = "Model is required") }
            valid = false
        }
        val year = state.year.trim().toIntOrNull()
        if (year == null || year < 1980 || year > 2030) {
            _uiState.update { it.copy(yearError = "Enter a valid year (1980–2030)") }
            valid = false
        }
        val odometer = state.currentOdometer.trim().toIntOrNull()
        if (odometer == null || odometer < 0) {
            _uiState.update { it.copy(odometerError = "Enter a valid odometer reading") }
            valid = false
        }
        return valid
    }
}
