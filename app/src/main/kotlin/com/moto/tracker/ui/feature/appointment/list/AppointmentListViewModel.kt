package com.moto.tracker.ui.feature.appointment.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.AppointmentStatus
import com.moto.tracker.domain.model.ServiceAppointment
import com.moto.tracker.domain.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppointmentListUiState(
    val upcoming: List<ServiceAppointment> = emptyList(),
    val past: List<ServiceAppointment> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AppointmentListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appointmentRepository: AppointmentRepository
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])

    val uiState: StateFlow<AppointmentListUiState> = appointmentRepository
        .observeByVehicle(vehicleId)
        .map { appointments ->
            val now = System.currentTimeMillis()
            val sorted = appointments.sortedByDescending { it.appointmentDate }
            AppointmentListUiState(
                upcoming = sorted.filter { it.status == AppointmentStatus.UPCOMING && it.appointmentDate >= now },
                past = sorted.filter { it.status != AppointmentStatus.UPCOMING || it.appointmentDate < now },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AppointmentListUiState(isLoading = true)
        )

    fun onMarkCompleted(appointment: ServiceAppointment) {
        viewModelScope.launch {
            appointmentRepository.updateStatus(appointment.id, AppointmentStatus.COMPLETED)
        }
    }

    fun onDeleteAppointment(appointment: ServiceAppointment) {
        viewModelScope.launch { appointmentRepository.delete(appointment) }
    }
}
