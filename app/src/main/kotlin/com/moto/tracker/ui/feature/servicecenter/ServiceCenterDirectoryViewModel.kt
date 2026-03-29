package com.moto.tracker.ui.feature.servicecenter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.ServiceCenter
import com.moto.tracker.domain.usecase.servicecenter.DeleteServiceCenterUseCase
import com.moto.tracker.domain.usecase.servicecenter.GetServiceCentersUseCase
import com.moto.tracker.domain.usecase.servicecenter.SetServiceCenterFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServiceCenterDirectoryUiState(
    val centers: List<ServiceCenter> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class ServiceCenterDirectoryViewModel @Inject constructor(
    private val getServiceCenters: GetServiceCentersUseCase,
    private val deleteServiceCenter: DeleteServiceCenterUseCase,
    private val setFavorite: SetServiceCenterFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServiceCenterDirectoryUiState())
    val uiState: StateFlow<ServiceCenterDirectoryUiState> = _uiState

    init {
        getServiceCenters()
            .onEach { centers ->
                _uiState.update { it.copy(centers = centers, isLoading = false) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
            .launchIn(viewModelScope)
    }

    fun delete(center: ServiceCenter) {
        viewModelScope.launch {
            runCatching { deleteServiceCenter(center) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun toggleFavorite(center: ServiceCenter) {
        viewModelScope.launch {
            runCatching { setFavorite(center.id, !center.isFavorite) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }
}
