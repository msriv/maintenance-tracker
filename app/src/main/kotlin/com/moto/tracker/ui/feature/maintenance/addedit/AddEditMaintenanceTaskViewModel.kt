package com.moto.tracker.ui.feature.maintenance.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.MaintenanceTask
import com.moto.tracker.domain.model.MaintenanceTaskType
import com.moto.tracker.domain.repository.MaintenanceRepository
import com.moto.tracker.domain.usecase.maintenance.AddMaintenanceTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTaskUiState(
    val taskType: MaintenanceTaskType = MaintenanceTaskType.ENGINE_OIL,
    val customName: String = "",
    val thresholdDays: String = "",
    val thresholdKm: String = "",
    val lastPerformedDate: Long? = null,
    val lastPerformedOdometer: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isEditMode: Boolean = false
)

sealed class AddEditTaskEvent {
    object NavigateBack : AddEditTaskEvent()
}

@HiltViewModel
class AddEditMaintenanceTaskViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val maintenanceRepository: MaintenanceRepository,
    private val addMaintenanceTask: AddMaintenanceTaskUseCase
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])
    private val taskId: Long? = savedStateHandle.get<Long>("taskId")?.takeIf { it != -1L }

    private val _uiState = MutableStateFlow(AddEditTaskUiState(isEditMode = taskId != null))
    val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

    private val _events = Channel<AddEditTaskEvent>()
    val events = _events.receiveAsFlow()

    init {
        taskId?.let { loadTask(it) }
    }

    private fun loadTask(id: Long) {
        viewModelScope.launch {
            val task = maintenanceRepository.getTaskById(id) ?: return@launch
            _uiState.update {
                it.copy(
                    taskType = task.taskType,
                    customName = task.customName ?: "",
                    thresholdDays = task.thresholdDays?.toString() ?: "",
                    thresholdKm = task.thresholdKm?.toString() ?: "",
                    lastPerformedDate = task.lastPerformedDate,
                    lastPerformedOdometer = task.lastPerformedOdometer?.toString() ?: "",
                    notes = task.notes ?: ""
                )
            }
        }
    }

    fun onTaskTypeChange(value: MaintenanceTaskType) = _uiState.update { it.copy(taskType = value) }
    fun onCustomNameChange(value: String) = _uiState.update { it.copy(customName = value) }
    fun onThresholdDaysChange(value: String) = _uiState.update { it.copy(thresholdDays = value) }
    fun onThresholdKmChange(value: String) = _uiState.update { it.copy(thresholdKm = value) }
    fun onLastPerformedDateChange(value: Long?) = _uiState.update { it.copy(lastPerformedDate = value) }
    fun onLastPerformedOdometerChange(value: String) = _uiState.update { it.copy(lastPerformedOdometer = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun onSave() {
        val state = _uiState.value
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val now = System.currentTimeMillis()
            val task = MaintenanceTask(
                id = taskId ?: 0,
                vehicleId = vehicleId,
                taskType = state.taskType,
                customName = if (state.taskType == MaintenanceTaskType.CUSTOM) state.customName.ifBlank { null } else null,
                thresholdDays = state.thresholdDays.toIntOrNull(),
                thresholdKm = state.thresholdKm.toIntOrNull(),
                lastPerformedDate = state.lastPerformedDate,
                lastPerformedOdometer = state.lastPerformedOdometer.toIntOrNull(),
                notes = state.notes.ifBlank { null },
                createdAt = if (taskId == null) now else 0L,
                updatedAt = now
            )

            val result = if (taskId == null) {
                addMaintenanceTask(task)
            } else {
                runCatching { maintenanceRepository.updateTask(task) }
            }
            result.onSuccess { _events.send(AddEditTaskEvent.NavigateBack) }
                .onFailure { _uiState.update { it.copy(isLoading = false) } }
        }
    }
}
