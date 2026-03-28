package com.moto.tracker.ui.feature.appointment.addedit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.AppointmentStatus
import com.moto.tracker.domain.model.ServiceAppointment
import com.moto.tracker.domain.repository.AppointmentRepository
import com.moto.tracker.domain.repository.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditAppointmentUiState(
    val title: String = "",
    val appointmentDate: Long = System.currentTimeMillis() + 86_400_000L, // tomorrow
    val serviceCenterName: String = "",
    val serviceCenterPhone: String = "",
    val estimatedCost: String = "",
    val reminderEnabled: Boolean = true,
    val reminderMinutesBefore: Int = 1440,
    val notes: String = "",
    val isEditMode: Boolean = false,
    val isLoading: Boolean = false,
    val titleError: String? = null
)

sealed class AddEditAppointmentEvent {
    object NavigateBack : AddEditAppointmentEvent()
}

@HiltViewModel
class AddEditAppointmentViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appointmentRepository: AppointmentRepository,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])
    private val appointmentId: Long = savedStateHandle["appointmentId"] ?: -1L

    private val _uiState = MutableStateFlow(AddEditAppointmentUiState())
    val uiState: StateFlow<AddEditAppointmentUiState> = _uiState.asStateFlow()

    private val _events = Channel<AddEditAppointmentEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (appointmentId > 0) {
            viewModelScope.launch {
                val existing = appointmentRepository.getById(appointmentId)
                if (existing != null) {
                    _uiState.update {
                        it.copy(
                            title = existing.title,
                            appointmentDate = existing.appointmentDate,
                            serviceCenterName = existing.serviceCenterName ?: "",
                            serviceCenterPhone = existing.serviceCenterPhone ?: "",
                            estimatedCost = existing.estimatedCost?.toString() ?: "",
                            reminderEnabled = existing.reminderEnabled,
                            reminderMinutesBefore = existing.reminderMinutesBefore,
                            notes = existing.notes ?: "",
                            isEditMode = true
                        )
                    }
                }
            }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value, titleError = null) }
    fun onAppointmentDateChange(millis: Long) = _uiState.update { it.copy(appointmentDate = millis) }
    fun onServiceCenterNameChange(value: String) = _uiState.update { it.copy(serviceCenterName = value) }
    fun onServiceCenterPhoneChange(value: String) = _uiState.update { it.copy(serviceCenterPhone = value) }
    fun onEstimatedCostChange(value: String) = _uiState.update { it.copy(estimatedCost = value) }
    fun onReminderEnabledChange(value: Boolean) = _uiState.update { it.copy(reminderEnabled = value) }
    fun onReminderMinutesBeforeChange(value: Int) = _uiState.update { it.copy(reminderMinutesBefore = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun onSave() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = "Title is required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val now = System.currentTimeMillis()
            val appointment = ServiceAppointment(
                id = if (state.isEditMode) appointmentId else 0,
                vehicleId = vehicleId,
                appointmentDate = state.appointmentDate,
                title = state.title.trim(),
                serviceCenterName = state.serviceCenterName.ifBlank { null },
                serviceCenterPhone = state.serviceCenterPhone.ifBlank { null },
                estimatedCost = state.estimatedCost.toDoubleOrNull(),
                status = AppointmentStatus.UPCOMING,
                reminderEnabled = state.reminderEnabled,
                reminderMinutesBefore = state.reminderMinutesBefore,
                notes = state.notes.ifBlank { null },
                createdAt = now,
                updatedAt = now
            )

            if (state.isEditMode) {
                appointmentRepository.update(appointment)
            } else {
                val id = appointmentRepository.insert(appointment)
                // Schedule reminder if enabled and appointment is in the future
                if (state.reminderEnabled && state.appointmentDate > now) {
                    notificationScheduler.scheduleAppointmentReminder(
                        appointment.copy(id = id)
                    )
                }
            }

            _events.send(AddEditAppointmentEvent.NavigateBack)
        }
    }
}
