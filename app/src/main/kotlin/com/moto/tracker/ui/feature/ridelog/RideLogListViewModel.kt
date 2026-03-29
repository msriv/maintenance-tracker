package com.moto.tracker.ui.feature.ridelog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.moto.tracker.RideLogListDestination
import com.moto.tracker.domain.model.RideLog
import com.moto.tracker.domain.usecase.ridelog.DeleteRideLogUseCase
import com.moto.tracker.domain.usecase.ridelog.GetRideLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class RideLogListUiState(
    val rides: List<RideLog> = emptyList(),
    val totalKmThisMonth: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class RideLogListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRideLogs: GetRideLogsUseCase,
    private val deleteRideLog: DeleteRideLogUseCase
) : ViewModel() {

    private val vehicleId: Long = savedStateHandle.toRoute<RideLogListDestination>().vehicleId

    private val _uiState = MutableStateFlow(RideLogListUiState())
    val uiState: StateFlow<RideLogListUiState> = _uiState

    init {
        getRideLogs(vehicleId)
            .onEach { rides ->
                val startOfMonth = getStartOfCurrentMonth()
                val totalKm = rides
                    .filter { it.date >= startOfMonth }
                    .sumOf { it.kmDriven }
                _uiState.update {
                    it.copy(
                        rides = rides.sortedByDescending { r -> r.date },
                        totalKmThisMonth = totalKm,
                        isLoading = false
                    )
                }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)
    }

    private fun getStartOfCurrentMonth(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun deleteRide(ride: RideLog) {
        viewModelScope.launch {
            runCatching { deleteRideLog(ride) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }
}
