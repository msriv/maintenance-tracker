package com.moto.tracker.ui.feature.maintenance.list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.DuenessStatus
import com.moto.tracker.domain.model.MaintenanceTask
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.repository.MaintenanceRepository
import com.moto.tracker.domain.repository.VehicleRepository
import com.moto.tracker.domain.usecase.maintenance.GetMaintenanceTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MaintenanceListUiState(
    val vehicle: Vehicle? = null,
    val overdueTasks: List<MaintenanceTask> = emptyList(),
    val dueSoonTasks: List<MaintenanceTask> = emptyList(),
    val upcomingTasks: List<MaintenanceTask> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MaintenanceListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMaintenanceTasks: GetMaintenanceTasksUseCase,
    private val vehicleRepository: VehicleRepository,
    private val maintenanceRepository: MaintenanceRepository
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])

    val uiState: StateFlow<MaintenanceListUiState> = combine(
        vehicleRepository.observeById(vehicleId),
        getMaintenanceTasks(vehicleId)
    ) { vehicle, tasks ->
        val currentOdometer = vehicle?.currentOdometer ?: 0
        MaintenanceListUiState(
            vehicle = vehicle,
            overdueTasks = tasks.filter { it.duenessStatus(currentOdometer) == DuenessStatus.OVERDUE },
            dueSoonTasks = tasks.filter { it.duenessStatus(currentOdometer) == DuenessStatus.DUE_SOON },
            upcomingTasks = tasks.filter { it.duenessStatus(currentOdometer) == DuenessStatus.OK },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MaintenanceListUiState(isLoading = true)
    )

    fun onDeactivateTask(task: MaintenanceTask) {
        viewModelScope.launch {
            maintenanceRepository.deactivateTask(task.id)
        }
    }
}
