package com.moto.tracker.ui.feature.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.repository.ExportRepository
import com.moto.tracker.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

enum class ExportFormat { PDF, CSV }

data class ExportUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: Long? = null,
    val selectedFormat: ExportFormat = ExportFormat.PDF,
    val fromDate: Long = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0)
    }.timeInMillis,
    val toDate: Long = System.currentTimeMillis(),
    val isExporting: Boolean = false,
    val error: String? = null
)

sealed class ExportEvent {
    data class ShareFile(val uri: Uri, val mimeType: String) : ExportEvent()
    data class ShowError(val message: String) : ExportEvent()
}

@HiltViewModel
class ExportViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val vehicleRepository: VehicleRepository,
    private val exportRepository: ExportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExportUiState())
    val uiState: StateFlow<ExportUiState> = _uiState.asStateFlow()

    private val _events = Channel<ExportEvent>()
    val events = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            vehicleRepository.observeAll().collect { vehicles ->
                _uiState.update { it.copy(vehicles = vehicles) }
            }
        }
    }

    fun onVehicleSelected(id: Long?) = _uiState.update { it.copy(selectedVehicleId = id) }
    fun onFormatSelected(format: ExportFormat) = _uiState.update { it.copy(selectedFormat = format) }
    fun onFromDateChange(millis: Long) = _uiState.update { it.copy(fromDate = millis) }
    fun onToDateChange(millis: Long) = _uiState.update { it.copy(toDate = millis) }

    fun onExport() {
        val state = _uiState.value
        val vehicleId = state.selectedVehicleId
        if (vehicleId == null) {
            viewModelScope.launch { _events.send(ExportEvent.ShowError("Please select a vehicle")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isExporting = true, error = null) }
            runCatching {
                val file = when (state.selectedFormat) {
                    ExportFormat.PDF -> exportRepository.exportToPdf(vehicleId, state.fromDate, state.toDate)
                    ExportFormat.CSV -> exportRepository.exportToCsv(vehicleId, state.fromDate, state.toDate)
                }
                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                val mimeType = when (state.selectedFormat) {
                    ExportFormat.PDF -> "application/pdf"
                    ExportFormat.CSV -> "text/csv"
                }
                _events.send(ExportEvent.ShareFile(uri, mimeType))
            }.onFailure { e ->
                _events.send(ExportEvent.ShowError(e.message ?: "Export failed"))
            }
            _uiState.update { it.copy(isExporting = false) }
        }
    }
}
