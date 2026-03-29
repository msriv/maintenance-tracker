package com.moto.tracker.ui.feature.recall

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.moto.tracker.AppNavHost
import com.moto.tracker.RecallAlertsDestination
import com.moto.tracker.domain.model.RecallAlert
import com.moto.tracker.domain.usecase.recall.AcknowledgeRecallUseCase
import com.moto.tracker.domain.usecase.recall.GetRecallAlertsUseCase
import com.moto.tracker.domain.usecase.recall.SyncRecallsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecallAlertsUiState(
    val recalls: List<RecallAlert> = emptyList(),
    val isLoading: Boolean = true,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class RecallAlertsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRecallAlerts: GetRecallAlertsUseCase,
    private val syncRecallsUseCase: SyncRecallsUseCase,
    private val acknowledgeRecallUseCase: AcknowledgeRecallUseCase
) : ViewModel() {

    private val vehicleId: Long = savedStateHandle.toRoute<RecallAlertsDestination>().vehicleId

    private val _uiState = MutableStateFlow(RecallAlertsUiState())
    val uiState: StateFlow<RecallAlertsUiState> = _uiState

    init {
        getRecallAlerts(vehicleId)
            .onEach { recalls ->
                _uiState.update { it.copy(recalls = recalls, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun syncRecalls(make: String, model: String, year: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, syncMessage = null) }
            val result = syncRecallsUseCase(vehicleId, make, model, year)
            result.fold(
                onSuccess = { count ->
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            syncMessage = if (count > 0) "Found $count recall(s)" else "No new recalls found"
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isSyncing = false, errorMessage = e.message ?: "Sync failed")
                    }
                }
            )
        }
    }

    fun acknowledge(recallId: Long) {
        viewModelScope.launch {
            acknowledgeRecallUseCase(recallId)
        }
    }
}
