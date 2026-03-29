package com.moto.tracker.ui.feature.ridelog.add

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.moto.tracker.AddRideLogDestination
import com.moto.tracker.domain.model.RideLog
import com.moto.tracker.domain.usecase.ridelog.LogRideUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddRideLogUiState(
    val date: Long = System.currentTimeMillis(),
    val startOdometer: String = "",
    val endOdometer: String = "",
    val tripName: String = "",
    val notes: String = "",
    val isSaving: Boolean = false,
    val errorMessage: String? = null
) {
    val kmDriven: Int?
        get() {
            val start = startOdometer.toIntOrNull() ?: return null
            val end = endOdometer.toIntOrNull() ?: return null
            return if (end > start) end - start else null
        }
}

@HiltViewModel
class AddRideLogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val logRide: LogRideUseCase
) : ViewModel() {

    private val vehicleId: Long = savedStateHandle.toRoute<AddRideLogDestination>().vehicleId

    private val _uiState = MutableStateFlow(AddRideLogUiState())
    val uiState: StateFlow<AddRideLogUiState> = _uiState

    fun updateDate(value: Long) = _uiState.update { it.copy(date = value) }
    fun updateStartOdometer(value: String) = _uiState.update { it.copy(startOdometer = value) }
    fun updateEndOdometer(value: String) = _uiState.update { it.copy(endOdometer = value) }
    fun updateTripName(value: String) = _uiState.update { it.copy(tripName = value) }
    fun updateNotes(value: String) = _uiState.update { it.copy(notes = value) }

    fun save(onSaved: () -> Unit) {
        val state = _uiState.value
        val startOdo = state.startOdometer.toIntOrNull()
        val endOdo = state.endOdometer.toIntOrNull()

        if (startOdo == null) {
            _uiState.update { it.copy(errorMessage = "Enter a valid start odometer") }
            return
        }
        if (endOdo == null) {
            _uiState.update { it.copy(errorMessage = "Enter a valid end odometer") }
            return
        }
        if (endOdo <= startOdo) {
            _uiState.update { it.copy(errorMessage = "End odometer must be greater than start odometer") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val now = System.currentTimeMillis()
            val ride = RideLog(
                vehicleId = vehicleId,
                date = state.date,
                startOdometer = startOdo,
                endOdometer = endOdo,
                kmDriven = endOdo - startOdo,
                tripName = state.tripName.trim().ifBlank { null },
                notes = state.notes.trim().ifBlank { null },
                createdAt = now
            )
            runCatching { logRide(ride) }
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false) }
                    onSaved()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false, errorMessage = e.message ?: "Failed to save ride") }
                }
        }
    }
}
