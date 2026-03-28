package com.moto.tracker.ui.feature.maintenance.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.ManufacturerSchedule
import com.moto.tracker.domain.repository.ManufacturerScheduleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ManufacturerScheduleUiState(
    val makes: List<String> = emptyList(),
    val models: List<String> = emptyList(),
    val selectedMake: String? = null,
    val selectedModel: String? = null,
    val schedules: List<ManufacturerSchedule> = emptyList()
)

@HiltViewModel
class ManufacturerScheduleViewModel @Inject constructor(
    private val repository: ManufacturerScheduleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManufacturerScheduleUiState())
    val uiState: StateFlow<ManufacturerScheduleUiState> = _uiState.asStateFlow()

    private var schedulesJob: kotlinx.coroutines.Job? = null

    init {
        viewModelScope.launch {
            val makes = repository.getDistinctMakes()
            _uiState.update { it.copy(makes = makes) }
        }
    }

    fun onMakeSelected(make: String) {
        viewModelScope.launch {
            val models = repository.getModelsForMake(make)
            _uiState.update { it.copy(selectedMake = make, models = models, selectedModel = null, schedules = emptyList()) }
        }
    }

    fun onModelSelected(model: String) {
        val make = _uiState.value.selectedMake ?: return
        _uiState.update { it.copy(selectedModel = model) }

        schedulesJob?.cancel()
        schedulesJob = viewModelScope.launch {
            repository.observeScheduleForVehicle(make, model, 2024)
                .collect { schedules ->
                    _uiState.update { it.copy(schedules = schedules) }
                }
        }
    }
}
