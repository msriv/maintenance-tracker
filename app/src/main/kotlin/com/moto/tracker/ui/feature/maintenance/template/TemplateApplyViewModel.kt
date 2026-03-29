package com.moto.tracker.ui.feature.maintenance.template

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.moto.tracker.TemplateApplyDestination
import com.moto.tracker.domain.model.BestPractice
import com.moto.tracker.domain.model.RegistrySchedule
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.repository.VehicleRepository
import com.moto.tracker.domain.usecase.registry.ApplyOemScheduleUseCase
import com.moto.tracker.domain.usecase.registry.GetBestPracticeTemplatesUseCase
import com.moto.tracker.domain.usecase.registry.GetRegistryModelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TemplateApplyUiState(
    val vehicle: Vehicle? = null,
    val oemSchedules: List<RegistrySchedule> = emptyList(),
    val bestPracticeTemplates: List<BestPractice> = emptyList(),
    val isLoading: Boolean = true,
    val isApplying: Boolean = false,
    val appliedCount: Int? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class TemplateApplyViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vehicleRepository: VehicleRepository,
    private val applyOemSchedule: ApplyOemScheduleUseCase,
    private val getBestPracticeTemplates: GetBestPracticeTemplatesUseCase,
    private val getRegistryModels: GetRegistryModelsUseCase
) : ViewModel() {

    private val vehicleId: Long = savedStateHandle.toRoute<TemplateApplyDestination>().vehicleId

    private val _uiState = MutableStateFlow(TemplateApplyUiState())
    val uiState: StateFlow<TemplateApplyUiState> = _uiState

    init {
        viewModelScope.launch {
            val vehicle = vehicleRepository.getById(vehicleId)
            _uiState.update { it.copy(vehicle = vehicle) }
        }

        viewModelScope.launch {
            getBestPracticeTemplates()
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { practices ->
                    _uiState.update {
                        it.copy(
                            bestPracticeTemplates = practices.filter { p -> p.isTemplate },
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun applyOemSchedule() {
        val vehicle = _uiState.value.vehicle ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isApplying = true, appliedCount = null) }
            val result = applyOemSchedule(
                vehicleId = vehicleId,
                make = vehicle.make,
                model = vehicle.model,
                year = vehicle.year
            )
            result.fold(
                onSuccess = { count ->
                    _uiState.update { it.copy(isApplying = false, appliedCount = count) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isApplying = false, errorMessage = e.message ?: "Failed to apply schedule")
                    }
                }
            )
        }
    }

    fun clearAppliedCount() {
        _uiState.update { it.copy(appliedCount = null) }
    }
}
