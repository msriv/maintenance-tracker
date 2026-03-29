package com.moto.tracker.ui.feature.seasonal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.SeasonalChecklist
import com.moto.tracker.domain.usecase.seasonal.DeleteSeasonalChecklistUseCase
import com.moto.tracker.domain.usecase.seasonal.GetSeasonalChecklistsUseCase
import com.moto.tracker.domain.usecase.seasonal.GetSeasonalTemplatesUseCase
import com.moto.tracker.domain.usecase.seasonal.UpsertSeasonalChecklistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SeasonalChecklistsUiState(
    val templates: List<SeasonalChecklist> = emptyList(),
    val myChecklists: List<SeasonalChecklist> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

@HiltViewModel
class SeasonalChecklistsViewModel @Inject constructor(
    private val getSeasonalTemplates: GetSeasonalTemplatesUseCase,
    private val getSeasonalChecklists: GetSeasonalChecklistsUseCase,
    private val upsertSeasonalChecklist: UpsertSeasonalChecklistUseCase,
    private val deleteSeasonalChecklist: DeleteSeasonalChecklistUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SeasonalChecklistsUiState())
    val uiState: StateFlow<SeasonalChecklistsUiState> = _uiState

    init {
        combine(
            getSeasonalTemplates(),
            getSeasonalChecklists()
        ) { templates, all ->
            val myChecklists = all.filter { !it.isTemplate }
            _uiState.update {
                it.copy(
                    templates = templates,
                    myChecklists = myChecklists,
                    isLoading = false
                )
            }
        }
            .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
            .launchIn(viewModelScope)
    }

    fun useTemplate(template: SeasonalChecklist) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val newChecklist = template.copy(
                id = 0L,
                isTemplate = false,
                isCompleted = false,
                completedAt = null,
                createdAt = now,
                updatedAt = now
            )
            runCatching { upsertSeasonalChecklist(newChecklist) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    fun deleteChecklist(checklist: SeasonalChecklist) {
        viewModelScope.launch {
            runCatching { deleteSeasonalChecklist(checklist) }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }
}
