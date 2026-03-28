package com.moto.tracker.ui.feature.maintenance.kmlog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.KmLog
import com.moto.tracker.domain.repository.MaintenanceRepository
import com.moto.tracker.domain.repository.VehicleRepository
import com.moto.tracker.domain.usecase.kmlog.LogDailyKmUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KmLogUiState(
    val currentOdometer: Int = 0,
    val odometer: String = "",
    val notes: String = "",
    val recentLogs: List<KmLog> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

sealed class KmLogEvent {
    object LogSaved : KmLogEvent()
}

@HiltViewModel
class KmLogViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vehicleRepository: VehicleRepository,
    private val maintenanceRepository: MaintenanceRepository,
    private val logDailyKm: LogDailyKmUseCase
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])

    private val _uiState = MutableStateFlow(KmLogUiState())
    val uiState: StateFlow<KmLogUiState> = _uiState.asStateFlow()

    private val _events = Channel<KmLogEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            vehicleRepository.observeById(vehicleId).collect { vehicle ->
                _uiState.update { it.copy(currentOdometer = vehicle?.currentOdometer ?: 0) }
            }
        }
        viewModelScope.launch {
            maintenanceRepository.observeKmLogsByVehicle(vehicleId).collect { logs ->
                _uiState.update { it.copy(recentLogs = logs.take(20)) }
            }
        }
    }

    fun onOdometerChange(value: String) = _uiState.update { it.copy(odometer = value, error = null, successMessage = null) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun onSave() {
        val odometer = _uiState.value.odometer.toIntOrNull()
        if (odometer == null || odometer < 0) {
            _uiState.update { it.copy(error = "Enter a valid odometer reading") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            logDailyKm(vehicleId, odometer, _uiState.value.notes.ifBlank { null })
                .onSuccess { log ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            odometer = "",
                            notes = "",
                            successMessage = "Logged +${log.kmDriven} km today"
                        )
                    }
                    _events.send(KmLogEvent.LogSaved)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
        }
    }
}
