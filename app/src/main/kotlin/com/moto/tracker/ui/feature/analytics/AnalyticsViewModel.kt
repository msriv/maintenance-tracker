package com.moto.tracker.ui.feature.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moto.tracker.domain.model.CategoryExpense
import com.moto.tracker.domain.model.MonthlyExpense
import com.moto.tracker.domain.model.Vehicle
import com.moto.tracker.domain.repository.ExpenseRepository
import com.moto.tracker.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class AnalyticsUiState(
    val vehicles: List<Vehicle> = emptyList(),
    val selectedVehicleId: Long? = null,
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val categoryTotals: List<CategoryExpense> = emptyList(),
    val monthlyTrend: List<MonthlyExpense> = emptyList(),
    val totalAmount: Double = 0.0,
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val expenseRepository: ExpenseRepository
) : ViewModel() {

    private val _selectedVehicleId = MutableStateFlow<Long?>(null)
    private val _selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))

    val uiState: StateFlow<AnalyticsUiState> = vehicleRepository.observeAll()
        .combine(_selectedVehicleId) { vehicles, vehicleId -> vehicles to vehicleId }
        .combine(_selectedYear) { (vehicles, vehicleId), year -> Triple(vehicles, vehicleId, year) }
        .map { (vehicles, vehicleId, year) ->
            val cal = Calendar.getInstance()
            cal.set(year, Calendar.JANUARY, 1, 0, 0, 0)
            val from = cal.timeInMillis
            cal.set(year, Calendar.DECEMBER, 31, 23, 59, 59)
            val to = cal.timeInMillis

            val categoryTotals = expenseRepository.getCategoryTotals(vehicleId, from, to)
            val monthlyTrend = expenseRepository.getMonthlyTrend(vehicleId, year)

            AnalyticsUiState(
                vehicles = vehicles,
                selectedVehicleId = vehicleId,
                selectedYear = year,
                categoryTotals = categoryTotals,
                monthlyTrend = monthlyTrend,
                totalAmount = categoryTotals.sumOf { it.amount },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AnalyticsUiState(isLoading = true)
        )

    fun onVehicleSelected(vehicleId: Long?) {
        _selectedVehicleId.value = vehicleId
    }

    fun onYearSelected(year: Int) {
        _selectedYear.value = year
    }
}
