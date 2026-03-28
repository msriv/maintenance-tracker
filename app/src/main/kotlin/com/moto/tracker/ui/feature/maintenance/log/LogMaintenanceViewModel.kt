package com.moto.tracker.ui.feature.maintenance.log

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.MaintenanceLog
import com.moto.tracker.domain.repository.MaintenanceRepository
import com.moto.tracker.domain.usecase.maintenance.LogMaintenanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LogMaintenanceUiState(
    val taskName: String = "Service",
    val odometer: String = "",
    val cost: String = "",
    val mechanicName: String = "",
    val serviceCenterName: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val odometerError: String? = null
)

sealed class LogMaintenanceEvent {
    object NavigateBack : LogMaintenanceEvent()
}

@HiltViewModel
class LogMaintenanceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val maintenanceRepository: MaintenanceRepository,
    private val logMaintenanceUseCase: LogMaintenanceUseCase
) : ViewModel() {

    private val taskId: Long = requireNotNull(savedStateHandle["taskId"])

    private val _uiState = MutableStateFlow(LogMaintenanceUiState())
    val uiState: StateFlow<LogMaintenanceUiState> = _uiState.asStateFlow()

    private val _events = Channel<LogMaintenanceEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val task = maintenanceRepository.getTaskById(taskId)
            _uiState.update { it.copy(taskName = task?.displayName ?: "Service") }
        }
    }

    fun onOdometerChange(value: String) = _uiState.update { it.copy(odometer = value, odometerError = null) }
    fun onCostChange(value: String) = _uiState.update { it.copy(cost = value) }
    fun onMechanicNameChange(value: String) = _uiState.update { it.copy(mechanicName = value) }
    fun onServiceCenterNameChange(value: String) = _uiState.update { it.copy(serviceCenterName = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun onSave() {
        val state = _uiState.value
        val odometer = state.odometer.toIntOrNull()
        if (odometer == null || odometer < 0) {
            _uiState.update { it.copy(odometerError = "Enter a valid odometer reading") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val task = maintenanceRepository.getTaskById(taskId) ?: return@launch
            val now = System.currentTimeMillis()
            val log = MaintenanceLog(
                taskId = taskId,
                vehicleId = task.vehicleId,
                performedDate = now,
                odometer = odometer,
                cost = state.cost.toDoubleOrNull(),
                mechanicName = state.mechanicName.ifBlank { null },
                serviceCenterName = state.serviceCenterName.ifBlank { null },
                notes = state.notes.ifBlank { null },
                createdAt = now
            )

            logMaintenanceUseCase(log)
                .onSuccess { _events.send(LogMaintenanceEvent.NavigateBack) }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }
}
