package com.moto.tracker.ui.feature.backup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.BackupStatus
import com.moto.tracker.domain.usecase.backup.BackupToCloudUseCase
import com.moto.tracker.domain.usecase.backup.GetBackupStatusUseCase
import com.moto.tracker.domain.usecase.backup.RestoreFromCloudUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CloudBackupUiState(
    val backupStatus: BackupStatus = BackupStatus(),
    val isLoading: Boolean = true,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class CloudBackupViewModel @Inject constructor(
    private val getBackupStatus: GetBackupStatusUseCase,
    private val backupToCloud: BackupToCloudUseCase,
    private val restoreFromCloud: RestoreFromCloudUseCase
) : ViewModel() {

    private val _extraState = MutableStateFlow(
        Pair<String?, String?>(null, null) // successMessage to errorMessage
    )

    val uiState: StateFlow<CloudBackupUiState> = combine(
        getBackupStatus(),
        _extraState
    ) { status, extra ->
        CloudBackupUiState(
            backupStatus = status,
            isLoading = false,
            successMessage = extra.first,
            errorMessage = extra.second
        )
    }
        .catch { e ->
            emit(CloudBackupUiState(isLoading = false, errorMessage = e.message))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CloudBackupUiState(isLoading = true)
        )

    fun backup() {
        viewModelScope.launch {
            _extraState.update { Pair(null, null) }
            val result = backupToCloud()
            result.fold(
                onSuccess = { _extraState.update { Pair("Backup completed successfully", null) } },
                onFailure = { e -> _extraState.update { Pair(null, e.message ?: "Backup failed") } }
            )
        }
    }

    fun restore() {
        viewModelScope.launch {
            _extraState.update { Pair(null, null) }
            val result = restoreFromCloud()
            result.fold(
                onSuccess = { _extraState.update { Pair("Restore completed successfully", null) } },
                onFailure = { e -> _extraState.update { Pair(null, e.message ?: "Restore failed") } }
            )
        }
    }

    fun clearMessages() {
        _extraState.update { Pair(null, null) }
    }
}
