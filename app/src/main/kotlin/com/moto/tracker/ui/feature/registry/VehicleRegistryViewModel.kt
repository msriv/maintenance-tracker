package com.moto.tracker.ui.feature.registry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.RegistryMake
import com.moto.tracker.domain.model.RegistryModel
import com.moto.tracker.domain.model.VehicleType
import com.moto.tracker.domain.usecase.registry.GetRegistryMakesUseCase
import com.moto.tracker.domain.usecase.registry.GetRegistryModelsUseCase
import com.moto.tracker.domain.usecase.registry.SyncRegistryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VehicleRegistryUiState(
    val makes: List<RegistryMake> = emptyList(),
    val selectedMake: RegistryMake? = null,
    val models: List<RegistryModel> = emptyList(),
    val searchQuery: String = "",
    val typeFilter: VehicleType? = null,
    val isSyncing: Boolean = false,
    val errorMessage: String? = null
) {
    val filteredMakes: List<RegistryMake>
        get() = if (searchQuery.isBlank()) makes
        else makes.filter { it.name.contains(searchQuery, ignoreCase = true) }

    val filteredModels: List<RegistryModel>
        get() {
            var result = if (typeFilter != null) models.filter { it.vehicleType == typeFilter } else models
            if (searchQuery.isNotBlank()) result = result.filter {
                it.model.contains(searchQuery, ignoreCase = true) ||
                    it.variants.any { v -> v.contains(searchQuery, ignoreCase = true) }
            }
            return result
        }
}

@HiltViewModel
class VehicleRegistryViewModel @Inject constructor(
    private val getRegistryMakes: GetRegistryMakesUseCase,
    private val getRegistryModels: GetRegistryModelsUseCase,
    private val syncRegistry: SyncRegistryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(VehicleRegistryUiState())
    val uiState: StateFlow<VehicleRegistryUiState> = _uiState

    private var modelsJob: Job? = null

    init {
        getRegistryMakes()
            .onEach { makes ->
                _uiState.update { it.copy(makes = makes) }
                if (makes.isEmpty()) {
                    syncRegistry()
                }
            }
            .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
            .launchIn(viewModelScope)
    }

    fun selectMake(make: RegistryMake) {
        _uiState.update { it.copy(selectedMake = make, models = emptyList(), searchQuery = "") }
        modelsJob?.cancel()
        modelsJob = getRegistryModels(make.name)
            .onEach { models ->
                _uiState.update { it.copy(models = models) }
            }
            .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
            .launchIn(viewModelScope)
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedMake = null, models = emptyList(), searchQuery = "") }
        modelsJob?.cancel()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setTypeFilter(type: VehicleType?) {
        _uiState.update { it.copy(typeFilter = type) }
    }

    fun syncRegistry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, errorMessage = null) }
            val result = syncRegistry.invoke()
            result.fold(
                onSuccess = { _uiState.update { it.copy(isSyncing = false) } },
                onFailure = { e ->
                    _uiState.update { it.copy(isSyncing = false, errorMessage = e.message ?: "Sync failed") }
                }
            )
        }
    }
}
