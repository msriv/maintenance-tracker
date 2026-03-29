package com.moto.tracker.ui.feature.vehicle.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.MileagePrediction
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.repository.VehicleRepository
import com.moto.tracker.domain.usecase.prediction.GetMileagePredictionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VehicleDetailUiState(
    val vehicle: Vehicle? = null,
    val mileagePrediction: MileagePrediction? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class VehicleDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vehicleRepository: VehicleRepository,
    private val getMileagePrediction: GetMileagePredictionUseCase
) : ViewModel() {

    private val vehicleId: Long = requireNotNull(savedStateHandle["vehicleId"])

    private val _predictionState = MutableStateFlow<MileagePrediction?>(null)

    val uiState: StateFlow<VehicleDetailUiState> = combine(
        vehicleRepository.observeById(vehicleId),
        _predictionState
    ) { vehicle, prediction ->
        VehicleDetailUiState(vehicle = vehicle, mileagePrediction = prediction, isLoading = false)
    }
        .catch { emit(VehicleDetailUiState(isLoading = false)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = VehicleDetailUiState(isLoading = true)
        )

    init {
        viewModelScope.launch {
            val prediction = runCatching { getMileagePrediction(vehicleId) }.getOrNull()
            _predictionState.update { prediction }
        }
    }
}
